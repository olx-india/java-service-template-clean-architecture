package com.olx.boilerplate.it;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public final class IntegrationTestContext {

    private static final ThreadLocal<IntegrationTestContext> CONTEXT = ThreadLocal.withInitial(IntegrationTestContext::new);

    private String baseUrl = "http://localhost:8080";
    private String path = "";
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private String requestBody;
    private Response response;
    private final Map<String, String> namedBodies = new HashMap<>();

    public static IntegrationTestContext get() {
        return CONTEXT.get();
    }

    public static void reset() {
        CONTEXT.remove();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void setNamedBody(String name, String body) {
        namedBodies.put(name, body);
    }

    public String getNamedBody(String name) {
        return namedBodies.get(name);
    }
}
