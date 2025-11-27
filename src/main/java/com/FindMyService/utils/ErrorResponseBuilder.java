package com.FindMyService.utils;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ErrorResponseBuilder {

    private ErrorResponseBuilder() {}

    public static Map<String, Object> build(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }

    public static Map<String, Object> forbidden(String message) {
        return build(HttpStatus.FORBIDDEN, message);
    }
}
