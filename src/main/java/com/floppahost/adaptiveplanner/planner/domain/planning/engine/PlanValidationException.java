package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

/**
 * Exception thrown when a day plan violates validation rules.
 */
public class PlanValidationException extends IllegalArgumentException {
    
    public PlanValidationException(String message) {
        super(message);
    }

}
