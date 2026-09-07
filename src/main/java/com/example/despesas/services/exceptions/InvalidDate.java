package com.example.despesas.services.exceptions;

public class InvalidDate extends RuntimeException {
    public InvalidDate(String message) {
        super(message);
    }
}
