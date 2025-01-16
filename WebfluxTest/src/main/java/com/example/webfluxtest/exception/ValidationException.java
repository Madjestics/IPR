package com.example.webfluxtest.exception;

/**
 * Ошибка валидации
 */
public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
