package com.example.despesas.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Object id) {
        super("Despesa não encontrada, Id: " + id);
    }

}
