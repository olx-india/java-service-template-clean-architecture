package com.olx.boilerplate.infrastructure.appConfig.tenant;

import com.olx.boilerplate.infrastructure.appConfig.tenant.codes.BrandCode;
import com.olx.boilerplate.infrastructure.appConfig.tenant.codes.CountryCode;

import java.util.Objects;

public class Tenant {

    public static final String DEFAULT = "X-Default-Tenant";

    private final BrandCode brand;
    private final CountryCode country;
    private boolean isDefault = false;

    public Tenant(String tenant) {
        if (tenant == null) {
            throw new IllegalArgumentException("Tenant cannot be null");
        }

        /*
         * This is used to handle a special case for filterValuesConfiguration where default means any country
         */
        if (DEFAULT.equals(tenant)) {
            this.brand = null;
            this.country = null;
            isDefault = true;

            return;
        }

        try {
            this.brand = BrandCode.valueOf(tenant.substring(0, tenant.length() - 2).toUpperCase());
            this.country = CountryCode.valueOf(tenant.substring(tenant.length() - 2).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Tenant value");
        }
    }

    public BrandCode getBrand() {
        return brand;
    }

    public CountryCode getCountry() {
        return country;
    }

    public String key() {
        return getBrand().name().toLowerCase() + getCountry().name().toLowerCase();
    }

    public String name() {
        return getBrand().name() + getCountry().name();
    }

    @Override
    public String toString() {
        return (isDefault) ? DEFAULT : getBrand().name().toLowerCase() + getCountry().name().toLowerCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Tenant tenant = (Tenant) obj;
        if (this.brand == null && this.country == null && tenant.brand == null && tenant.country == null) {
            return true;
        }

        return toString().equals(tenant.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, country);
    }

}
