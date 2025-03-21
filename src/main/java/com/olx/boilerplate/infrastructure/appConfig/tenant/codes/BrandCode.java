package com.olx.boilerplate.infrastructure.appConfig.tenant.codes;

public enum BrandCode {

    TENANT;

    public String key() {
        return name().toLowerCase();
    }

}
