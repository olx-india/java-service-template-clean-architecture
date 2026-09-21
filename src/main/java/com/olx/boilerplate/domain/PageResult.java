package com.olx.boilerplate.domain;

import java.util.List;

/**
 * Domain pagination result — keeps Spring Data types out of repository ports.
 */
public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / (double) size);
        return new PageResult<>(List.copyOf(content), page, size, totalElements, totalPages);
    }
}
