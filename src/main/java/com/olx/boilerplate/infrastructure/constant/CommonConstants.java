package com.olx.boilerplate.infrastructure.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonConstants {

    public static final String testServiceCircuitBreaker = "testServiceCircuitBreaker";
    public static final String READ = "_read";

    public static final String DB_CACHE_PREP_STATEMENTS = "cachePrepStmts";
    public static final String DB_USER_SERVER_PREP_STATEMENTS = "useServerPrepStmts";
    public static final String DB_PREP_STATEMENT_CACHE_SIZE = "prepStmtCacheSize";
    public static final String DB_PREP_STATEMENT_CACHE_SQL_LIMIT = "prepStmtCacheSqlLimit";

    public static final String MESSAGE = "message";

    public static final String DEFAULT = "default";
    public static final String X_PUBLISHER_ID = "x-publisher-id";
}

// NOTE : Having Constant files is an Antipattern in clean code philosply we should avoid add all constant in global
// Contants files, we would only move the to global file when there are multiple usage across layers.
