package com.library.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.hibernate.query.QueryTypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    static final String TIME_STAMP="timestamp";
    static final String STATUS="status";
    static final String ERROR="error";
    static final String MESSAGE="message";

    ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        String path=((ServletWebRequest)request).getRequest().getRequestURI();
        logger.error("Exception caught: {} | Status: {} | Path: {}", ex.getMessage(), status, path);
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put(TIME_STAMP, LocalDateTime.now());
        errorResponse.put(STATUS, status.value());
        errorResponse.put(ERROR, status.getReasonPhrase());
        errorResponse.put(MESSAGE, ex.getMessage());
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException ex) {
        try {
            Map<String, Object> errorMap = objectMapper.readValue(ex.contentUTF8(), LinkedHashMap.class);
            int statusCode = (int) errorMap.getOrDefault(STATUS, 500);
            return new ResponseEntity<>(errorMap, HttpStatus.valueOf(statusCode));
        } catch (Exception e) {
            logger.error("Failed to parse downstream error: {}", e.getMessage());
            Map<String, Object> fallbackResponse = new LinkedHashMap<>();
            fallbackResponse.put(TIME_STAMP, LocalDateTime.now());
            fallbackResponse.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
            fallbackResponse.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            fallbackResponse.put(MESSAGE, "Downstream service error: " + ex.getMessage());
            return new ResponseEntity<>(fallbackResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String path=((ServletWebRequest)request).getRequest().getRequestURI();
        logger.warn("Validation failed: {} | Path: {}", ex.getMessage(), path);

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(TIME_STAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put(MESSAGE, "Validation failed");
        response.put("details", errors);
        response.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FieldNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFieldNotFoundException(FieldNotFoundException ex, WebRequest request) {
        String path=((ServletWebRequest)request).getRequest().getRequestURI();
        logger.warn("FieldNotFoundException: {} | Path: {}", ex.getMessage(),path);
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        String path=((ServletWebRequest)request).getRequest().getRequestURI();
        logger.warn("FieldNotFoundException: {} | Path: {}", ex.getMessage(),path);
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(QueryTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleQueryTypeMismatchException(QueryTypeMismatchException ex, WebRequest request) {
        String path=((ServletWebRequest)request).getRequest().getRequestURI();
        logger.error("QueryTypeMismatchException: {} | Path: {}", ex.getMessage(), path);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String path=((ServletWebRequest)request).getRequest().getRequestURI();
        logger.error("RuntimeException: {} | Path: {}", ex.getMessage(), path);
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceExistsException(ResourceExistsException ex, WebRequest request) {
        String path=((ServletWebRequest)request).getRequest().getRequestURI();
        logger.warn("ResourceExistsException: {} | Path: {}", ex.getMessage(), path);
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        String path=((ServletWebRequest)request).getRequest().getRequestURI();
        logger.error("Unhandled Exception: {} | Path: {}", ex.getMessage(), path);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
