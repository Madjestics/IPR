package com.example.webfluxtest.exception;

/**
 * Выбрасывается, в случае необработанных ошибок
 */
public class InternalServerException extends RuntimeException{
    public InternalServerException(String message) {
        super(message);
    }
}
