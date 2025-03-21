package com.olx.boilerplate.infrastructure.exceptions;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Error error;

    @Data
    public static class Error implements Serializable {

        private String code;
        private String message;
    }
}
