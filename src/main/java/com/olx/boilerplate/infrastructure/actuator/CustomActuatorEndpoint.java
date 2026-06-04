package com.olx.boilerplate.infrastructure.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "custom")
public class CustomActuatorEndpoint {

    @ReadOperation
    public Map<String, String> info() {
        return Map.of(
                      "service", "boilerplate",
                      "architecture", "clean-architecture",
                      "status", "ok");
    }
}
