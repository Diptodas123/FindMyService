package com.FindMyService.utils;

import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ResponseBuilder {

    private ResponseBuilder() {}

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
    public static Map<String, Object> unauthorized(String message) {
        return build(HttpStatus.UNAUTHORIZED, message);
    }

    public static Map<String, Object> conflict(String message) {
        return build(HttpStatus.CONFLICT, message);
    }

    public static Map<String, Object> serverError(String message) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static Map<String, Object> created(String message) {
        return build(HttpStatus.CREATED, message);
    }

    public static Map<String, Object> ok(String message) {
        return build(HttpStatus.OK, message);
    }

    public static Map<String, Object> notFound(String message) {
        return build(HttpStatus.NOT_FOUND, message);
    }

    public static Map<String, Object> badRequest(String message) {
        return build(HttpStatus.BAD_REQUEST, message);
    }

    public static Map<String, Object> internalServerError(String message) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
