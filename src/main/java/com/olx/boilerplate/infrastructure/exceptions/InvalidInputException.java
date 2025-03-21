package com.olx.boilerplate.infrastructure.exceptions;

import java.util.Set;

import static java.lang.String.format;
import static java.lang.String.join;

@SuppressWarnings("unused")
public class InvalidInputException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Invalid values for field %s: %s.";

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String fieldName, String fieldValue) {
        super(format(MESSAGE_TEMPLATE, fieldName, fieldValue));
    }

    public InvalidInputException(Set<String> errors) {
        super(join(", ", errors));
    }

}
