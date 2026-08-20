package com.syncria.module.contact.exception;

import com.syncria.shared.exception.NotFoundException;

public class ContactNotFoundException extends NotFoundException {
    public ContactNotFoundException(Long id) {
        super("Contact not found with id: " + id);
    }
}
