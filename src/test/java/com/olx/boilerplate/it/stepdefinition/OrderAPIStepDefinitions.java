package com.olx.boilerplate.it.stepdefinition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olx.boilerplate.controller.dto.order.response.OrderResponse;
import com.olx.boilerplate.it.IntegrationTestContext;
import com.olx.boilerplate.it.IntegrationTestContextHolder;
import com.olx.boilerplate.usecase.order.command.CreateOrderCommand;
import com.olx.boilerplate.usecase.order.command.UpdateOrderCommand;
import io.cucumber.java.en.And;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderAPIStepDefinitions {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IntegrationTestContextHolder contextHolder;

    @And("I generate a CreateOrderRequest")
    public void setCreateOrderRequestBody() throws JsonProcessingException {
        CreateOrderCommand request = new CreateOrderCommand("Test User", 1, 50.0);
        context().setNamedBody("CreateOrderRequest", objectMapper.writeValueAsString(request));
    }

    @And("I generate an UpdateOrderRequest with id {} and quantity {}")
    public void setUpdateOrderRequestBody(String id, int quantity) throws JsonProcessingException {
        UpdateOrderCommand request = new UpdateOrderCommand(Long.parseLong(id), null, quantity, null);
        context().setNamedBody("UpdateOrderRequest", objectMapper.writeValueAsString(request));
    }

    @And("Validate order response")
    public void validateOrderResponse() {
        var response = context().getResponse().as(OrderResponse.class);
        var isValidResponse = response.getId() != null && StringUtils.isNoneBlank(response.getProduct());
        Assertions.assertTrue(isValidResponse, "Order response not valid");
    }

    @And("Validate order response with product {}")
    public void validateOrderResponseWithProduct(String product) {
        var response = context().getResponse().as(OrderResponse.class);
        var isValidResponse = response.getId() != null && StringUtils.isNoneBlank(response.getProduct());
        Assertions.assertTrue(isValidResponse, "Order response not valid");
        Assertions.assertEquals(product, response.getProduct(), "product don't match");
    }

    private IntegrationTestContext context() {
        return contextHolder.getContext();
    }
}
