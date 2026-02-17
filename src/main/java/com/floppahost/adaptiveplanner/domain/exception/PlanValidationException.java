package com.floppahost.adaptiveplanner.domain.exception;

/**
 * Exception thrown when a day plan violates validation rules.
 */
public class PlanValidationException extends IllegalArgumentException {
    
    public PlanValidationException(String message) {
        super(message);
    }

}
