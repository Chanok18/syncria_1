package com.syncria.module.pet.exception;

import com.syncria.shared.exception.NotFoundException;

public class PetNotFoundException extends NotFoundException {
    public PetNotFoundException(Long id) {
        super("Pet not found with id: " + id);
    }
}
