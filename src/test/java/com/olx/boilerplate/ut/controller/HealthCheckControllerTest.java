package com.olx.boilerplate.ut.controller;

import com.olx.boilerplate.controller.HealthCheckController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class HealthCheckControllerTest {

  private HealthCheckController healthCheckController;

  @BeforeEach
  void setUp() {
    this.healthCheckController = new HealthCheckController();
  }

  @Test
  void ping() {
    var response = healthCheckController.ping();
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
