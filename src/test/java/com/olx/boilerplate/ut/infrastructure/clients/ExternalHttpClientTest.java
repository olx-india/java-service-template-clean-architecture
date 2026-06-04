package com.olx.boilerplate.ut.infrastructure.clients;

import com.olx.boilerplate.infrastructure.clients.ExternalHttpClient;
import com.olx.boilerplate.infrastructure.exceptions.ExternalServiceException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExternalHttpClientTest {

    private WireMockServer wireMockServer;
    private ExternalHttpClient client;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        client = new ExternalHttpClient(new OkHttpClient());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void fetch_ShouldReturnResponseBody() {
        wireMockServer.stubFor(get(urlEqualTo("/api/data"))
                        .willReturn(aResponse().withStatus(200).withBody("{\"ok\":true}")));

        String result = client.fetch(wireMockServer.baseUrl() + "/api/data");

        assertEquals("{\"ok\":true}", result);
    }

    @Test
    void fetch_ShouldThrowOnServerError() {
        wireMockServer.stubFor(get(urlEqualTo("/api/fail"))
                        .willReturn(aResponse().withStatus(500)));

        assertThrows(ExternalServiceException.class,
                     () -> client.fetch(wireMockServer.baseUrl() + "/api/fail"));
    }
}
