package com.example.starwarsplanets.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import com.example.starwarsplanets.error.ErrorResponse;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    StringBuilder message = new StringBuilder();

    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMsg = error.getDefaultMessage();

      if (!message.isEmpty()) {
        message.append("; ");
      }

      message.append(fieldName).append(": ").append(errorMsg);
    });

    logger.warn("Validation error: {}", message);

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
        "Validation Failed", message.toString(), request.getRequestURI(), LocalDateTime.now());

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDatabaseConflictExceptions(
      DataIntegrityViolationException ex, HttpServletRequest request) {

    logger.error("Database integrity violation", ex);

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(),
        "Database Conflict", "A resource with this name already exists", request.getRequestURI(),
        LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatchException(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

    Class<?> requiredType = ex.getRequiredType();
    String requiredTypeName =
        (requiredType != null) ? requiredType.getSimpleName() : "the expected type";
    String paramName = Objects.toString(ex.getName(), "parameter");
    String message = String.format("%s should be of type %s", paramName, requiredTypeName);

    logger.warn("Type mismatch error: {}", message);

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
        "Invalid Parameter Type", message, request.getRequestURI(), LocalDateTime.now());

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
      HttpServletRequest request) {

    logger.error("Unexpected error", ex);

    ErrorResponse errorResponse =
        new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
            "An unexpected error occurred", request.getRequestURI(), LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
