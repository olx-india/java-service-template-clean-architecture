package com.olx.boilerplate.infrastructure.exceptions;

import static java.lang.String.format;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(format("Unexpected error in an underlying service: %s", message));
    }

}
