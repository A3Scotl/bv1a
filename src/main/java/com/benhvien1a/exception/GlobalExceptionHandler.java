package com.benhvien1a.exception;

import com.benhvien1a.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        String errorMessage = "Xác thực thất bại: " + errors.toString();
        logger.error(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                false,
                errorMessage,
                null,
                errorMessage,
                ZonedDateTime.now(ZoneId.of("UTC")),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("Truy cập bị từ chối: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(
                false,
                "Bạn không có quyền thực hiện hành động này",
                null,
                ex.getMessage(),
                ZonedDateTime.now(ZoneId.of("UTC")),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        ));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthException(AuthException ex, WebRequest request) {
        logger.error("Lỗi xác thực: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                false,
                ex.getMessage(),
                null,
                ex.getMessage(),
                ZonedDateTime.now(ZoneId.of("UTC")),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        logger.error("Lỗi thời gian chạy: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                false,
                "Đã xảy ra lỗi: " + ex.getMessage(),
                null,
                ex.getMessage(),
                ZonedDateTime.now(ZoneId.of("UTC")),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex, WebRequest request) {
        logger.error("Lỗi không mong muốn: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                false,
                "Đã xảy ra lỗi không mong muốn",
                null,
                ex.getMessage(),
                ZonedDateTime.now(ZoneId.of("UTC")),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        ));
    }
}