package com.olx.boilerplate.it.stepdefinition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olx.boilerplate.controller.dto.user.response.UserResponse;
import com.olx.boilerplate.it.IntegrationTestContext;
import com.olx.boilerplate.it.IntegrationTestContextHolder;
import com.olx.boilerplate.usecase.users.command.CreateUserCommand;
import com.olx.boilerplate.usecase.users.command.UpdateUserCommand;
import io.cucumber.java.en.And;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAPIStepDefinitions {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IntegrationTestContextHolder contextHolder;

    @And("I generate a CreateUserRequest")
    public void setCreateUserRequestBody() throws JsonProcessingException {
        CreateUserCommand request = new CreateUserCommand("Test User", "test@example.com");
        context().setNamedBody("CreateUserRequest", objectMapper.writeValueAsString(request));
    }

    @And("I generate an UpdateUserRequest with id {} and name {}")
    public void setUpdateUserRequestBody(String id, String name) throws JsonProcessingException {
        UpdateUserCommand request = new UpdateUserCommand(Long.parseLong(id), name, null);
        context().setNamedBody("UpdateUserRequest", objectMapper.writeValueAsString(request));
    }

    @And("Validate user response")
    public void validateUserResponse() {
        var response = context().getResponse().as(UserResponse.class);
        var isValidResponse = response.getId() != null
                        && StringUtils.isNoneBlank(response.getName(), response.getEmail());
        Assertions.assertTrue(isValidResponse, "User response not valid");
    }

    @And("Validate user response with name {}")
    public void validateUserResponseWithName(String name) {
        var response = context().getResponse().as(UserResponse.class);
        var isValidResponse = response.getId() != null
                        && StringUtils.isNoneBlank(response.getName(), response.getEmail());
        Assertions.assertTrue(isValidResponse, "User response not valid");
        Assertions.assertEquals(name, response.getName(), "Names don't match");
    }

    private IntegrationTestContext context() {
        return contextHolder.getContext();
    }
}
