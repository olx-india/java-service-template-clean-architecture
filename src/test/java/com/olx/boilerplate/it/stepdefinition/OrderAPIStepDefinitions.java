package com.olx.boilerplate.it.stepdefinition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olx.assertx.service.model.RequestType;
import com.olx.assertx.stepdefinitions.BaseRestStepDefinition;
import com.olx.boilerplate.controller.dto.order.request.CreateOrderRequest;
import com.olx.boilerplate.controller.dto.order.request.UpdateOrderRequest;
import com.olx.boilerplate.infrastructure.data.entities.OrderData;

import io.cucumber.java.en.And;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

public class OrderAPIStepDefinitions extends BaseRestStepDefinition {

    ObjectMapper objectMapper = Jackson.newObjectMapper();

    @And("I generate a CreateOrderRequest")
    public void setCreateOrderRequestBody() throws JsonProcessingException {
        CreateOrderRequest request = new CreateOrderRequest("Test User", 1, 50.0);

        // Debugging: Print JSON body
        String requestBody = objectMapper.writeValueAsString(request);
        System.out.println("Request Body: " + requestBody);

        testContext().set("CreateOrderRequest", requestBody);
    }


    @And("I generate an UpdateOrderRequest with id {} and quantity {}")
    public void setUpdateOrderRequestBody(String id, int quantity) throws JsonProcessingException {
        UpdateOrderRequest request = new UpdateOrderRequest(Long.parseLong(id));
        request.setQuantity(quantity);
        testContext().set("UpdateOrderRequest", objectMapper.writeValueAsString(request));
    }

    @And("Validate order response")
    public void validateOrderResponse() {
        var response = testContext().getResponse(RequestType.REST, OrderData.class);
        var isValidResponse = response.getId() != null && StringUtils.isNoneBlank(response.getProduct());
        Assert.assertTrue("Order response not valid", isValidResponse);
    }

    @And("Validate order response with product {}")
    public void validateUserResponseWithProduct(String product) {
        var response = testContext().getResponse(RequestType.REST, OrderData.class);
        var isValidResponse = response.getId() != null && StringUtils.isNoneBlank(response.getProduct());
        Assert.assertTrue("Order response not valid", isValidResponse);
        Assert.assertEquals("product don't match", product, response.getProduct());
    }

}
