package com.trail_race.race_application.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
final class Responses {

    // private constructor to prevent instantiation
    private Responses() { }


    static ResponseEntity<ErrorResponse> getAndLogDebug(Exception e, HttpServletRequest req, HttpStatus httpStatus) {

        return getAndLogDebug(e, null, req, httpStatus);
    }

    static ResponseEntity<ErrorResponse> getAndLogDebug(Exception e, String message, HttpServletRequest req,
                                                        HttpStatus httpStatus) {
        message = message != null ? message : sanitizeMessage(e);
        log.debug(message);
        return getError(e, message, req, httpStatus);
    }

    static ResponseEntity<ErrorResponse> getAndLogError(Exception e, HttpServletRequest req,
                                                        HttpStatus httpStatus) {
        return getAndLogError(e, null, req, httpStatus);
    }

    static ResponseEntity<ErrorResponse> getAndLogError(Exception e, String message, HttpServletRequest req,
                                                        HttpStatus httpStatus) {
        message = message != null ? message : sanitizeMessage(e);
        log.error(message, e);
        return getError(e, message, req, httpStatus);
    }

    private static ResponseEntity<ErrorResponse> getError(Exception e, String message, String resourceId,
                                                          HttpServletRequest req, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(ErrorResponse.builder()
                .message(message)
                .resourceId(resourceId)
                .exception(e.getClass().getName())
                .status(httpStatus.value())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString)
                        .collect(Collectors.joining(";\n")))
                .path(req.getContextPath() + "/" + req.getServletPath())
                .params(req.getParameterMap())
                .headers(getHeaders(req))
                .build());
    }

    private static ResponseEntity<ErrorResponse> getError(Exception e, String message, HttpServletRequest req,
                                                          HttpStatus httpStatus) {
        return getError(e, message, null, req, httpStatus);
    }

    private static Map<String, Object> getHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .filter(s -> !"authorization".equals(s))
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(request.getHeaders(h))
                ));
    }

    /**
     * Hide sql query from exception message in case of DataAccessException.
     */
    private static String sanitizeMessage(@NotNull Exception e) {
        if (e.getCause() != null && e instanceof DataAccessException) {
            return e.getCause().getMessage();
        }
        return e.getMessage();
    }
}
