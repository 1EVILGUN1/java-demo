package com.example.demo.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            errors.put(fieldName, violation.getMessage());
        }
        return errors;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (MessageSourceResolvable resolvable : ex.getAllErrors()) {
            if (resolvable instanceof FieldError fieldError) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else if (resolvable instanceof ObjectError objectError) {
                // Для параметров метода (например, @PathVariable)
                String paramName = objectError.getObjectName();
                errors.put(paramName, objectError.getDefaultMessage());
            } else {
                // Для случаев, когда resolvable не является ObjectError
                errors.put("error", resolvable.getDefaultMessage());
            }
        }
        return errors;
    }

    /**
     * Обработка пользовательских ошибок (например, "User not found", "Insufficient funds")
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    /**
     * Обработка ошибок бизнес-логики (например, "User must have at least one email")
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateException(IllegalStateException ex) {
        log.warn("Illegal state error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    /**
     * Обработка ошибок аутентификации
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    /**
     * Обработка ошибок авторизации (например, попытка изменить чужие данные)
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ErrorResponse("Access denied: " + ex.getMessage());
    }

    /**
     * Обработка всех непредвиденных ошибок
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneralException(Exception ex) {
        log.error("Internal server error: ", ex);
        return new ErrorResponse("An unexpected error occurred");
    }


    record ErrorResponse(String message) {
    }
}