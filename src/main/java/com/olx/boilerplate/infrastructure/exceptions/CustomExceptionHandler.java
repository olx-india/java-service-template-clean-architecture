package com.olx.boilerplate.infrastructure.exceptions;

import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.logging.AppLogger;
import com.olx.boilerplate.logging.AppLoggers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final AppLogger LOGGER = AppLoggers.getLogger(CustomExceptionHandler.class);

    private static final String MALFORMED_JSON = "Malformed JSON request.";
    private static final String GENERIC_ERROR_MESSAGE = "Something went wrong.";

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return this.buildProblemDetail(BAD_REQUEST, MALFORMED_JSON, "malformed-json");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : "Validation failed";
        return this.buildProblemDetail(BAD_REQUEST, message, "validation-failed");
    }

    @ExceptionHandler({ResourceNotFoundException.class, jakarta.persistence.EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFoundException(Exception e) {
        return this.buildProblemDetail(NOT_FOUND, e.getMessage(), "not-found");
    }

    @ExceptionHandler(ExternalServiceException.class)
    protected ResponseEntity<Object> handleUnhandledExternalException(ExternalServiceException e) {
        return this.buildProblemDetail(INTERNAL_SERVER_ERROR, e.getMessage(), "external-service");
    }

    @ExceptionHandler(InvalidInputException.class)
    protected ResponseEntity<Object> handleInvalidInputException(InvalidInputException e) {
        return this.buildProblemDetail(BAD_REQUEST, e.getMessage(), "invalid-input");
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<Object> handleUncaughtExceptions(Throwable e) {
        boolean local = activeProfiles != null && activeProfiles.contains("local");
        var message = local ? StringUtils.defaultIfBlank(e.getMessage(), GENERIC_ERROR_MESSAGE)
                        : GENERIC_ERROR_MESSAGE;
        LOGGER.error("Unhandled exception", e);
        return this.buildProblemDetail(INTERNAL_SERVER_ERROR, message, "internal-error");
    }

    private ResponseEntity<Object> buildProblemDetail(HttpStatus status, String message, String typeSuffix) {
        LOGGER.warn("Error Response - Status: {}, Message: {}", status, message);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, message);
        problem.setTitle(status.getReasonPhrase());
        problem.setType(URI.create("https://api.example.com/problems/" + typeSuffix));
        problem.setProperty("code", status.getReasonPhrase());

        return ResponseEntity.status(status).body(problem);
    }
}
