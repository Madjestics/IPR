package com.example.webfluxtest.exception;

/**
 * ошибка аутентификации
 */
public class AuthException extends RuntimeException{
    public AuthException(String message) {
        super(message);
    }
}
