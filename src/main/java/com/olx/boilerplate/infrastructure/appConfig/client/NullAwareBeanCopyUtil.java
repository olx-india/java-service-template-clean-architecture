package com.olx.boilerplate.infrastructure.appConfig.client;

import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.springframework.stereotype.Component;

@Component
public class NullAwareBeanCopyUtil extends BeanUtilsBean2 {

    @SneakyThrows
    @Override
    public void copyProperty(Object bean, String name, Object value) {
        Object destValue = BeanUtils.getProperty(bean, name);
        if (value == null || destValue != null) {
            return;
        }

        super.copyProperty(bean, name, value);
    }
}
