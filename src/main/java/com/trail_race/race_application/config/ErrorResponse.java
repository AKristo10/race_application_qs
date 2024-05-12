package com.trail_race.race_application.config;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class ErrorResponse implements Serializable {
    private String message;
    private String resourceId;
    private Integer status;
    private String exception;
    private String stacktrace;
    private String path;
    private Map<String, String[]> params;
    private Map<String, Object> headers;
}
