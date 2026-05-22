package com.revnu.backend.shared.util;

import java.time.Instant;

import com.revnu.backend.shared.exception.ApiResponse;

public final class ResponseUtil {

    private ResponseUtil() {
    }

    public static ApiResponse success(Object data) {
        return new ApiResponse(true, data, null, Instant.now().toString());
    }
}
