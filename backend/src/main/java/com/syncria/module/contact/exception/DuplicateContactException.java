package com.syncria.module.contact.exception;

public class DuplicateContactException extends RuntimeException {
    public DuplicateContactException(String email) {
        super("A contact with email '" + email + "' already exists");
    }
}
