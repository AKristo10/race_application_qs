package com.trail_race.race_application.config;


import com.trail_race.race_application.exception.EventNotFoundException;
import com.trail_race.race_application.exception.NotFoundException;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.naming.ServiceUnavailableException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalErrorHandler {


    @ResponseBody  // 400
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MaxUploadSizeExceededException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(HttpServletRequest req, Exception e) {
        if (e instanceof MethodArgumentNotValidException exc) {
            Map<String, String> errors = new HashMap<>();
            exc.getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });

            return Responses.getAndLogDebug(e, errors.toString(), req, HttpStatus.BAD_REQUEST);
        }

        return getAndLogDebug(req, e, HttpStatus.BAD_REQUEST);
    }


    @ResponseBody // 404
    @ExceptionHandler({
            NotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(HttpServletRequest req, Exception e) {
        return getAndLogDebug(req, e, HttpStatus.NOT_FOUND);
    }


    @ResponseBody  // 500
    @ExceptionHandler({
            UndeclaredThrowableException.class,
            EventNotFoundException.class,
            Exception.class
    })
    public ResponseEntity<ErrorResponse> handleGenericExceptions(HttpServletRequest req, Exception e) {
        if (e instanceof DataAccessException
                && isThrowableOrCauseInstanceOf(e.getCause(), SQLIntegrityConstraintViolationException.class)) {
            return handleBadRequestExceptions(req, e);
        }
        return getAndLogError(req, e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody // 503
    @ExceptionHandler({
            RetryableException.class,
            ServiceUnavailableException.class
    })
    public ResponseEntity<ErrorResponse> handleApiNotAvailable(HttpServletRequest req, Exception e) {
        return getAndLogError(req, e, HttpStatus.SERVICE_UNAVAILABLE);
    }

    private ResponseEntity<ErrorResponse> getAndLogDebug(HttpServletRequest req, Exception e, HttpStatus httpStatus) {

        return Responses.getAndLogDebug(e, req, httpStatus);
    }

    private ResponseEntity<ErrorResponse> getAndLogError(HttpServletRequest req, Exception e, HttpStatus httpStatus) {
        return Responses.getAndLogError(e, req, httpStatus);
    }

    private static boolean isThrowableOrCauseInstanceOf(Throwable e, Class<?> t) {
        return t.isInstance(e) || (e != null && t.isInstance(e.getCause()));
    }

}
