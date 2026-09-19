package com.olx.boilerplate.it.stepdefinition;

import tools.jackson.databind.json.JsonMapper;
import com.olx.boilerplate.it.IntegrationTestContext;
import com.olx.boilerplate.it.IntegrationTestContextHolder;
import io.cucumber.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class ClientAPIStepDefinitions {

    private final ObjectMapper objectMapper = new JsonMapper();

    @Autowired
    private IntegrationTestContextHolder contextHolder;

    @And("I generate a KafkaRequestBody")
    public void setKafkaRequestBody() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", "Test Kafka Message");
        payload.put("timestamp", System.currentTimeMillis());
        context().setNamedBody("KafkaRequestBody", objectMapper.writeValueAsString(payload));
    }

    private IntegrationTestContext context() {
        return contextHolder.getContext();
    }
}
