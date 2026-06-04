package com.olx.boilerplate.it.stepdefinition;

import com.olx.boilerplate.it.IntegrationTestContext;
import com.olx.boilerplate.it.IntegrationTestContextHolder;
import com.olx.boilerplate.infrastructure.components.TenantFilter;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonRestStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private IntegrationTestContextHolder contextHolder;

    @Given("I have http://0.0.0.0:{int}/ host")
    public void setHost(int ignoredPort) {
        context().setBaseUrl("http://localhost:" + port);
    }

    @Given("^I have (\\S+) API$")
    public void setApiPath(String path) {
        context().setPath(path.startsWith("/") ? path : "/" + path);
    }

    @And("I have following headers")
    public void setHeaders(List<String> headerRows) {
        for (int i = 0; i < headerRows.size(); i += 2) {
            context().getHeaders().put(headerRows.get(i), headerRows.get(i + 1));
        }
    }

    @And("I have a request body in {word}")
    public void setRequestBodyFromContext(String bodyName) {
        context().setRequestBody(context().getNamedBody(bodyName));
    }

    @And("I have following query parameters")
    public void setQueryParameters(Map<String, String> params) {
        context().getQueryParams().clear();
        context().getQueryParams().putAll(params);
    }

    @When("Execute GET request using REST")
    public void executeGetRequest() {
        executeRequest("GET");
    }

    @When("Execute POST request using REST")
    public void executePostRequest() {
        executeRequest("POST");
    }

    @When("Execute PUT request using REST")
    public void executePutRequest() {
        executeRequest("PUT");
    }

    @When("Execute DELETE request using REST")
    public void executeDeleteRequest() {
        executeRequest("DELETE");
    }

    @Then("Validate status code is: {int}")
    public void validateStatusCode(int expectedStatus) {
        assertEquals(expectedStatus, context().getResponse().getStatusCode());
    }

    private void executeRequest(String method) {
        RequestSpecification request = RestAssured.given()
                        .baseUri(context().getBaseUrl())
                        .header(TenantFilter.X_DEFAULT_TENANT, "default")
                        .contentType(ContentType.JSON);

        context().getHeaders().forEach(request::header);
        context().getQueryParams().forEach(request::queryParam);

        if (context().getRequestBody() != null && !"GET".equals(method) && !"DELETE".equals(method)) {
            request.body(context().getRequestBody());
        }

        Response response = switch (method) {
            case "GET" -> request.when().get(context().getPath());
            case "POST" -> request.when().post(context().getPath());
            case "PUT" -> request.when().put(context().getPath());
            case "DELETE" -> request.when().delete(context().getPath());
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };

        context().setResponse(response);
    }

    protected IntegrationTestContext context() {
        return contextHolder.getContext();
    }
}
