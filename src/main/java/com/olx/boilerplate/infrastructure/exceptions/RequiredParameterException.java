package com.olx.boilerplate.infrastructure.exceptions;

import static java.lang.String.format;

public class RequiredParameterException extends RuntimeException {

    @SuppressWarnings("unused")
    public static final String PARAMETER_TYPE_HEADER = "header";

    @SuppressWarnings("unused")
    public static final String PARAMETER_TYPE_BODY = "body";

    protected RequiredParameterException(String parameterType, String parameterName) {
        super(format("The %s parameter %s is required.", parameterType, parameterName));
    }

}
