package com.olx.boilerplate.infrastructure.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    private static final String MALFORMED_JSON = "Malformed JSON request.";
    private static final String GENERIC_ERROR_MESSAGE = "Something went wrong.";

    protected @NotNull ResponseEntity<Object> handleHttpMessageNotReadable(@NotNull HttpMessageNotReadableException ex,
            @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        return this.buildResponseEntity(BAD_REQUEST, MALFORMED_JSON);
    }

    @ExceptionHandler(RequiredParameterException.class)
    protected ResponseEntity<Object> handleRequiredParameterException(RequiredParameterException e) {
        return this.buildResponseEntity(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({ EntityNotFoundException.class, javax.persistence.EntityNotFoundException.class })
    protected ResponseEntity<Object> handleEntityNotFoundException(Exception e) {
        return this.buildResponseEntity(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    protected ResponseEntity<Object> handleUnhandledExternalException(ExternalServiceException e) {
        return this.buildResponseEntity(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    protected ResponseEntity<Object> handleInvalidInputException(InvalidInputException e) {
        return this.buildResponseEntity(BAD_REQUEST, e.getMessage());
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<Object> handleUncaughtExceptions(Throwable e) {
        var message = StringUtils.defaultIfBlank(e.getMessage(), GENERIC_ERROR_MESSAGE);
        return this.buildResponseEntity(INTERNAL_SERVER_ERROR, message);
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message) {
        LOGGER.warn("Error Response - Status: {}, Message: {}", status, message);

        var error = new ErrorResponse.Error();
        error.setCode(status.getReasonPhrase());
        error.setMessage(message);

        var responseBody = new ErrorResponse();
        responseBody.setError(error);

        return ResponseEntity.status(status).body(responseBody);
    }
}
