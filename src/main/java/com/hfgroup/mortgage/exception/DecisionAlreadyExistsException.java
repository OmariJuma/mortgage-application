package com.hfgroup.mortgage.exception;

public class DecisionAlreadyExistsException extends RuntimeException {
    public DecisionAlreadyExistsException(String message) {
        super(message);
    }
} 