package org.cosmetic.com.exception;

import lombok.extern.slf4j.Slf4j;
import org.cosmetic.com.dto.response.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // --- Generic Handler ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- Common Exception Handlers ---
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
            RuntimeException.class,
            UnsupportedOperationException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequestExceptions(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Object>> handleNullPointerException(NullPointerException ex) {
        log.error("NullPointerException: ", ex);
        return buildResponse("Null pointer exception occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Object>> handleIOException(IOException ex) {
        return buildResponse("File processing error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse("Access denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String message = "Unsupported Content-Type: " + ex.getContentType() +
                ". Supported types: " + ex.getSupportedMediaTypes();
        return buildResponse(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // --- Custom Application Exceptions ---
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailException(EmailException ex) {
        return buildResponse("Email error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountAlreadyVerified(AccountAlreadyVerifiedException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidToken(InvalidTokenException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyVerified(UserAlreadyVerifiedException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    // --- Validation ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return buildResponse(errors, HttpStatus.BAD_REQUEST);
    }

    // --- Common Builder ---
    private ResponseEntity<ApiResponse<Object>> buildResponse(String message, HttpStatus status) {
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .status(false)
                .message(message)
                .data(null)
                .build();
        return new ResponseEntity<>(response, status);
    }
}
