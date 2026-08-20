-- V4: Create appointments table for Sprint 05

CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    pet_id BIGINT NOT NULL REFERENCES pets(id),
    contact_id BIGINT NOT NULL REFERENCES contacts(id),
    title VARCHAR(255) NOT NULL,
    reason TEXT,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_appointments_company ON appointments(company_id);
CREATE INDEX idx_appointments_pet ON appointments(pet_id);
CREATE INDEX idx_appointments_contact ON appointments(contact_id);
CREATE INDEX idx_appointments_date ON appointments(company_id, appointment_date);
CREATE INDEX idx_appointments_status ON appointments(company_id, status);
