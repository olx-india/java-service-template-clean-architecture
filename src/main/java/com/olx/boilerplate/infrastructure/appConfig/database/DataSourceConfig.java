package com.olx.boilerplate.infrastructure.appConfig.database;

import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

@Setter
@Configuration
@ConfigurationProperties(prefix = "datasource")
public class DataSourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);

    private static final String MASTER_DATA_SOURCE = "masterDataSource";
    private static final String REPLICA_DATA_SOURCE = "replicaDataSource";

    private ConnectionProps masterDataSource;
    private ConnectionProps replicaDataSource;

    private Map<String, String> schemaNames;
    private Map<String, Map<String, ConnectionProps>> overrides;

    public String getSchemaNameForSitecode(String siteCode) {
        if (schemaNames == null) {
            return siteCode;
        }
        return schemaNames.getOrDefault(siteCode, siteCode);
    }

    public ConnectionProps getMasterDatasourceFor(String siteCode) {
        return patchAndGetDataSource(masterDataSource, siteCode, MASTER_DATA_SOURCE);
    }

    public ConnectionProps getReplicaDatasourceFor(String siteCode) {
        return patchAndGetDataSource(replicaDataSource, siteCode, REPLICA_DATA_SOURCE);
    }

    private ConnectionProps patchAndGetDataSource(ConnectionProps defaultProps, String siteCode, String type) {
        if (overrides.containsKey(siteCode) && overrides.get(siteCode).containsKey(type)) {
            try {
                var patchDataSource = overrides.get(siteCode).get(type);
                return ConnectionProps.mergeObjects(defaultProps, patchDataSource);
            } catch (Exception e) {
                LOGGER.warn("Exception while merging objects: {}", e.getMessage(), e);
            }
        }
        return defaultProps;
    }

    @Data
    public static class ConnectionProps implements Serializable {
        private String jdbcUrl;
        private String username;
        private String password;
        private int minimumIdle;
        private int maxPoolSize;
        private int connectionTimeout;
        private int idleTimeout;
        private int prepStmsCacheSqlLimit;
        private int prepStmsCacheSqlCacheSize;
        private boolean cachePrepStmts;
        private boolean useServerPrepStmts;
        private String poolNameSuffix;

        private static ConnectionProps mergeObjects(ConnectionProps mainObj, ConnectionProps patchObj)
                throws IllegalAccessException {
            var mergedObject = SerializationUtils.clone(mainObj);

            Field[] fields = patchObj.getClass().getFields();
            for (Field field : fields) {
                var value = field.get(patchObj);
                if (value == null || (value instanceof String && StringUtils.isBlank((String) value))) {
                    continue;
                }
                field.trySetAccessible();
                field.set(mergedObject, value);
            }
            return mergedObject;
        }
    }
}
