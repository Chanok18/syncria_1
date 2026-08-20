package com.syncria.module.pet.entity;

import com.syncria.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "pets")
public class Pet extends BaseEntity {

    @Column(name = "contact_id", nullable = false)
    private Long contactId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;

    private String breed;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String gender;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
