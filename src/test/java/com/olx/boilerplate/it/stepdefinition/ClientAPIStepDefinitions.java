package com.olx.boilerplate.it.stepdefinition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olx.assertx.stepdefinitions.BaseRestStepDefinition;
import io.cucumber.java.en.And;
import io.dropwizard.jackson.Jackson;

import java.util.HashMap;
import java.util.Map;

public class ClientAPIStepDefinitions extends BaseRestStepDefinition {

    ObjectMapper objectMapper = Jackson.newObjectMapper();

    @And("I generate a KafkaRequestBody")
    public void setKafkaRequestBody() throws JsonProcessingException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", "Test Kafka Message");
        payload.put("timestamp", System.currentTimeMillis());
        String requestBody = objectMapper.writeValueAsString(payload);
        testContext().set("KafkaRequestBody", requestBody);
    }
}
