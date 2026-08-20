package com.syncria.module.appointment.exception;

import com.syncria.shared.exception.NotFoundException;

public class AppointmentNotFoundException extends NotFoundException {
    public AppointmentNotFoundException(Long id) {
        super("Appointment not found with id: " + id);
    }
}
