package com.olx.boilerplate.it.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
public class CucumberSpringConfiguration {

    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("public")
            .withUsername("testDBUser")
            .withPassword("testDBPassword")
            .withInitScript("db/init-it.sql");

    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7"))
            .withExposedPorts(6379);

    private static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    static {
        MYSQL.start();
        REDIS.start();
        KAFKA.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("DB_HOST", MYSQL::getJdbcUrl);
        registry.add("DB_REPLICA_HOST", MYSQL::getJdbcUrl);
        registry.add("REDIS_HOST", REDIS::getHost);
        registry.add("redis.port", () -> REDIS.getMappedPort(6379).toString());
        registry.add("KAFKA_HOST", KAFKA::getBootstrapServers);
        registry.add("app.runningITMode", () -> "false");
    }
}
