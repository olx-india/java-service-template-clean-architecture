package com.olx.boilerplate.infrastructure.appConfig.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;

@Data
@Configuration
@ConfigurationProperties("clients")
public class ExternalClientsConfig {

    private BaseClientConfig defaultClientConfig;
    private BaseClientConfig testServiceClientConfig;

    /**
     * This method copies the missing props into service specific client configs from the default client config. This
     * should be repeated for all the services added in this class
     */
    @PostConstruct
    public void copyProps() throws InvocationTargetException, IllegalAccessException {
        NullAwareBeanCopyUtil nullAwareBeanCopyUtil = new NullAwareBeanCopyUtil();

        nullAwareBeanCopyUtil.copyProperties(testServiceClientConfig, defaultClientConfig);
    }
}
