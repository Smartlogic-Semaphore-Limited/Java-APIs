package com.smartlogic.concepts.client;

public class ConceptsException extends Exception {

    public ConceptsException(Exception e) {
        super(e.getMessage());
    }

    public ConceptsException(String message) {
        super(message);
    }
}
