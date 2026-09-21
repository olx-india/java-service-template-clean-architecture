package com.olx.boilerplate.domain;

/**
 * Domain pagination request — keeps Spring Data types out of repository ports.
 */
public record PageQuery(int page, int size) {

    public PageQuery {
        if (page < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }
        if (size < 1) {
            throw new IllegalArgumentException("size must be >= 1");
        }
    }

    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size);
    }
}
