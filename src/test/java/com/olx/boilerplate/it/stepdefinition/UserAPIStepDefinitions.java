package com.olx.boilerplate.it.stepdefinition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olx.assertx.service.model.RequestType;
import com.olx.assertx.stepdefinitions.BaseRestStepDefinition;
import com.olx.boilerplate.infrastructure.data.entities.UserData;
import com.olx.boilerplate.controller.dto.user.request.CreateUserRequest;
import com.olx.boilerplate.controller.dto.user.request.UpdateUserRequest;
import io.cucumber.java.en.And;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

public class UserAPIStepDefinitions extends BaseRestStepDefinition {

  ObjectMapper objectMapper = Jackson.newObjectMapper();

  @And("I generate a CreateUserRequest")
  public void setCreateUserRequestBody() throws JsonProcessingException {
    CreateUserRequest request = new CreateUserRequest("Test User", "testEmail");

    String requestBody = objectMapper.writeValueAsString(request);

    testContext().set("CreateUserRequest", requestBody);
  }


  @And("I generate an UpdateUserRequest with id {} and name {}")
  public void setUpdateUserRequestBody(String id, String name) throws JsonProcessingException {
    UpdateUserRequest request = new UpdateUserRequest(Long.parseLong(id));
    request.setName(name);
    testContext().set("UpdateUserRequest", objectMapper.writeValueAsString(request));
  }

  @And("Validate user response")
  public void validateUserResponse() {
    var response = testContext().getResponse(RequestType.REST, UserData.class);
    var isValidResponse = response.getId() != null && StringUtils.isNoneBlank(response.getName(), response.getEmail());
    Assert.assertTrue("User response not valid", isValidResponse);
  }

  @And("Validate user response with name {}")
  public void validateUserResponseWithName(String name) {
    var response = testContext().getResponse(RequestType.REST, UserData.class);
    var isValidResponse = response.getId() != null && StringUtils.isNoneBlank(response.getName(), response.getEmail());
    Assert.assertTrue("User response not valid", isValidResponse);
    Assert.assertEquals("Names don't match", name, response.getName());
  }
}
