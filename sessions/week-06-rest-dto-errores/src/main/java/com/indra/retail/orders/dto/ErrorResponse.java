package com.indra.retail.orders.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        List<String> errors
) {
    public ErrorResponse(int status, List<String> errors) {
        this(Instant.now(), status, errors);
    }

    public ErrorResponse(int status, String error) {
        this(Instant.now(), status, List.of(error));
    }
}
