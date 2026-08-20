-- V3: Create pets table for Sprint 04

CREATE TABLE pets (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    contact_id BIGINT NOT NULL REFERENCES contacts(id),
    name VARCHAR(255) NOT NULL,
    species VARCHAR(100) NOT NULL,
    breed VARCHAR(100),
    birth_date DATE,
    gender VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_pets_company ON pets(company_id);
CREATE INDEX idx_pets_contact ON pets(contact_id);
CREATE INDEX idx_pets_name ON pets(company_id, name);
