package com.olx.boilerplate.infrastructure.appConfig;

import com.olx.boilerplate.infrastructure.appConfig.client.CircuitBreakersConfig;
import com.olx.boilerplate.infrastructure.appConfig.client.ExternalClientsConfig;
import com.olx.boilerplate.infrastructure.appConfig.client.RetryConfiguration;
import com.olx.boilerplate.infrastructure.appConfig.database.DataSourceConfig;
import com.olx.boilerplate.infrastructure.appConfig.http.HttpConfig;
import com.olx.boilerplate.infrastructure.appConfig.redis.RedisConfig;
import com.olx.boilerplate.infrastructure.appConfig.redis.RedisMode;
import com.olx.boilerplate.infrastructure.data.config.CustomRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.olx.boilerplate.infrastructure.constant.CommonConstants.*;
import static java.time.Duration.ofMillis;

@Configuration
public class ServiceDependencyModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDependencyModule.class);

    /* Circuit Breakers Config */
    @Bean
    public CircuitBreaker defaultCircuitBreaker(ExternalClientsConfig externalClientsConfig) {
        var circuitBreakersConfig = externalClientsConfig.getDefaultClientConfig().getCircuitBreakerConfig();
        return buildCircuitBreaker(circuitBreakersConfig, "default");
    }

    @Bean(name = "testServiceCircuitBreaker")
    public CircuitBreaker testServiceCircuitBreaker(ExternalClientsConfig externalClientsConfig) {
        var circuitBreakersConfig = externalClientsConfig.getTestServiceClientConfig().getCircuitBreakerConfig();
        return buildCircuitBreaker(circuitBreakersConfig, testServiceCircuitBreaker);
    }

    private CircuitBreaker buildCircuitBreaker(CircuitBreakersConfig circuitBreakersConfig, String circuitBreakerName) {
        return CircuitBreakerRegistry.of(configureCircuitBreaker(circuitBreakersConfig))
                .circuitBreaker(circuitBreakerName);
    }

    private static CircuitBreakerConfig configureCircuitBreaker(CircuitBreakersConfig configuration) {
        return CircuitBreakerConfig.custom().recordExceptions(HttpClientErrorException.class, RuntimeException.class)
                .slidingWindow(configuration.getSlidingWindowSize(), configuration.getMinimumNumberOfCalls(),
                        configuration.getSlidingWindowType())
                .permittedNumberOfCallsInHalfOpenState(configuration.getPermittedNumberOfCallsInHalfOpenState())
                .failureRateThreshold(configuration.getFailureRateThreshold())
                .waitDurationInOpenState(ofMillis(configuration.getWaitDurationInOpenState()))
                .slowCallDurationThreshold(ofMillis(configuration.getSlowCallDurationThreshold()))
                .slowCallRateThreshold(configuration.getSlowCallRateThreshold()).build();
    }
    /* Circuit Breakers Config End */

    /* Retry Config Begin */
    @Bean
    public Retry defaultRetryStrategy(ExternalClientsConfig externalClientsConfig) {
        var defaultRetryConfiguration = externalClientsConfig.getDefaultClientConfig().getRetryConfiguration();
        return Retry.of(DEFAULT, buildRetryConfig(defaultRetryConfiguration));
    }

    @Bean(name = "service1RetryStrategy")
    public Retry service1RetryStrategy(ExternalClientsConfig externalClientsConfig) {
        var retryConfiguration = externalClientsConfig.getTestServiceClientConfig().getRetryConfiguration();
        var serviceName = externalClientsConfig.getTestServiceClientConfig().getServiceName();
        return Retry.of(serviceName, buildRetryConfig(retryConfiguration));
    }

    private RetryConfig buildRetryConfig(RetryConfiguration conf) {
        IntervalFunction intervalFunction = null;
        if (ObjectUtils.allNotNull(conf.getWaitInterval(), conf.getWaitIntervalMultiplier())) {
            intervalFunction = IntervalFunction.ofExponentialBackoff(conf.getWaitInterval(),
                    conf.getWaitIntervalMultiplier());
        }

        var retryStrategyBuilder = RetryConfig.custom().maxAttempts(conf.getMaxAttempts())
                .waitDuration(Duration.ofMillis(conf.getWaitDuration()));

        if (intervalFunction != null) {
            retryStrategyBuilder.intervalFunction(intervalFunction);
        }

        return retryStrategyBuilder.build();
    }

    /* Retry Config End */

    /* HTTP Config */
    @Bean
    public OkHttpClient okHTTPClient(HttpConfig httpConfig) {
        return new OkHttpClient.Builder().retryOnConnectionFailure(false)
                .readTimeout(httpConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(httpConfig.getMaxIdleConnectionsPerRoute(),
                        httpConfig.getMaxConnectionKeepAliveDurationInMins(), TimeUnit.MINUTES))
                .build();
    }

    /* Database Config */
    @Bean
    public DataSource dataSource(AppConfig appConfig, DataSourceConfig dataSourceConfig) throws InterruptedException {
        if (appConfig.isRunningITMode()) {
            LOGGER.info("Sleeping for 10 sec"); // Wait for DB to come up and tables to get created
            Thread.sleep(10000);
        }
        var tenants = appConfig.getTenants();
        var customDataSource = new CustomRoutingDataSource();

        var dataSourcesMap = getDataSourceHashMapForHikari(dataSourceConfig, tenants);
        customDataSource.setTargetDataSources(dataSourcesMap);

        if (!dataSourcesMap.isEmpty()) {
            // Any of the DataSource can be set as default. Hibernate uses it to identify dialects and other db props.
            var defaultDataSource = tenants.get(0);
            customDataSource.setDefaultTargetDataSource(dataSourcesMap.get(defaultDataSource));
        }

        return customDataSource;
    }

    private Map<Object, Object> getDataSourceHashMapForHikari(DataSourceConfig dataSourceConfig, List<String> tenants) {
        Map<Object, Object> dataSourcesMap = new HashMap<>();

        tenants.forEach(siteCode -> {
            dataSourcesMap.put(siteCode, buildDataSource(dataSourceConfig.getMasterDatasourceFor(siteCode)));
            dataSourcesMap.put(siteCode + READ, buildDataSource(dataSourceConfig.getReplicaDatasourceFor(siteCode)));
        });

        return dataSourcesMap;
    }

    private DataSource buildDataSource(DataSourceConfig.ConnectionProps connectionProps) {
        var jdbcConfig = new HikariConfig();

        jdbcConfig.setJdbcUrl(connectionProps.getJdbcUrl());
        jdbcConfig.setUsername(connectionProps.getUsername());
        jdbcConfig.setPassword(connectionProps.getPassword());
        jdbcConfig.setMinimumIdle(connectionProps.getMinimumIdle());
        jdbcConfig.setIdleTimeout(connectionProps.getIdleTimeout());
        jdbcConfig.setMaximumPoolSize(connectionProps.getMaxPoolSize());
        jdbcConfig.setConnectionTimeout(connectionProps.getConnectionTimeout());
        jdbcConfig.setPoolName(connectionProps.getPoolNameSuffix());
        jdbcConfig.addDataSourceProperty("cachePrepStmts", connectionProps.isCachePrepStmts());
        jdbcConfig.addDataSourceProperty("useServerPrepStmts", connectionProps.isUseServerPrepStmts());
        jdbcConfig.addDataSourceProperty("prepStmtCacheSize", connectionProps.getPrepStmsCacheSqlCacheSize());
        jdbcConfig.addDataSourceProperty("prepStmtCacheSqlLimit", connectionProps.getPrepStmsCacheSqlLimit());

        return new HikariDataSource(jdbcConfig);
    }

    /* HTTP Config End */

    /* Redis Config Begin */

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        final StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());

        return template;
    }

    @Bean
    public RedisConnectionFactory buildJedisConnectionFactory(RedisConfig redisConfig) {
        return redisConfig.getMode() == RedisMode.CLUSTER
                ? new JedisConnectionFactory(
                        new RedisClusterConfiguration(List.of(redisConfig.getHost() + ":" + redisConfig.getPort())))
                : new JedisConnectionFactory(
                        new RedisStandaloneConfiguration(redisConfig.getHost(), redisConfig.getPort()));
    }

    @Bean
    public CacheManager defaultCacheManager(RedisConfig redisConfig, RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(redisConfig.getTimeToLive())))
                .build();
    }
}
