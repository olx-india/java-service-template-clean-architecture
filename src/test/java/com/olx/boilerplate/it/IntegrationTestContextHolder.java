package com.olx.boilerplate.it;

import org.springframework.stereotype.Component;

@Component
public class IntegrationTestContextHolder {

    public IntegrationTestContext getContext() {
        return IntegrationTestContext.get();
    }
}
