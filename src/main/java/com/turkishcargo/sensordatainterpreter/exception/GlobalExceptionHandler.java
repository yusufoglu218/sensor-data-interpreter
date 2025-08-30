package com.turkishcargo.sensordatainterpreter.exception;

import com.turkishcargo.sensordatainterpreter.dto.error.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles cases where a required request parameter is missing.
     * Example: /api/v1/locations/some-device?startTime=... (endTime is missing)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, WebRequest request) {

        String message = "Required parameter '" + ex.getParameterName() + "' is missing.";
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, message, request.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles cases where a request parameter has the wrong type.
     * Example: ...?startTime=not-a-date
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String message = "Parameter '" + ex.getName() + "' should be of type '" + Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + "'.";
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, message, request.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * A catch-all handler for any other unexpected exceptions.
     * This prevents exposing stack traces or technical details to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        String message = "An unexpected internal server error occurred. Please try again later.";
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}