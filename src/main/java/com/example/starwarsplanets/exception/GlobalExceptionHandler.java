package com.example.starwarsplanets.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Map;
import com.example.starwarsplanets.error.ErrorResponse;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
        "Validation Failed", errors.toString(), request.getRequestURI());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDatabaseConflictExceptions(
      DataIntegrityViolationException ex, HttpServletRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(),
        "Database operation failed", "`name` value already exists", request.getRequestURI());

    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();
    errors.put(ex.getPropertyName(), ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
        "Validation Failed", errors.toString(), request.getRequestURI());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}
