package com.revnu.backend.shared.exception;

public record ErrorDetail(
        String code,
        String message,
        Object details
        ) {

}
