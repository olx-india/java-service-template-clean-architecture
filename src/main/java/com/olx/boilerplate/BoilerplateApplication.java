package com.olx.boilerplate;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoilerplateApplication {
    public static void main(String[] args) {
        var context = SpringApplication.run(BoilerplateApplication.class, args);

        Tracer tracer = GlobalOpenTelemetry.getTracer("boilerplate-app");
        Span span = tracer.spanBuilder("custom-span").startSpan();

        try {
            System.out.println("Tracing enabled!");
        } finally {
            span.end();
        }
    }
}
