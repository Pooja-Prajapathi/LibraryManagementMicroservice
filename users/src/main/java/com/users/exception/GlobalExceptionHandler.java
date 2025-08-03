package com.users.exception;

import org.hibernate.query.QueryTypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        logger.error("Exception caught: {} | Status: {} | Path: {}", ex.getMessage(), status, request.getDescription(false));

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Validation failed: {} | Path: {}", ex.getMessage(), request.getDescription(false));

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", "Validation failed");
        response.put("details", errors);
        response.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FieldNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFieldNotFoundException(FieldNotFoundException ex, WebRequest request) {
        if (logger.isWarnEnabled()) {
            logger.warn("FieldNotFoundException: {} | Path: {}", ex.getMessage(), request.getDescription(false));
        }
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(QueryTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleQueryTypeMismatchException(QueryTypeMismatchException ex, WebRequest request) {
        logger.error("QueryTypeMismatchException: {} | Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        logger.error("RuntimeException: {} | Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceExistsException(ResourceExistsException ex, WebRequest request) {
        logger.warn("ResourceExistsException: {} | Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }
}
