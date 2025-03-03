package com.backend.backend.model;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The Role class represents a user's role in the database.
 * Each role can be found in the 'roles' table in the database.
 */
@Entity
@Table(name = "roles")
public class Role {
    // ===============
    // Database Fields
    // ===============

    // Field 'id' is a PRIMARY KEY and the id auto-increments in the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Field 'name' stores enum values as strong, has natural id for searching, and a maximum column length of 64
    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 64)
    private RoleName name;

    // ============
    // Constructors
    // ============

    /**
     * Default Constructor required by JPA.
     */
    public Role() {

    }

    /**
     * Constructor to initialize a role with a specific name.
     * 
     * @param name The role name (e.g., admin, user, guest, etc.)
     */
    public Role(RoleName name) {
        this.name = name;
    }

    // =======
    // Getters
    // =======

    /**
     * Gets the role ID
     * 
     * @return The ID of the role
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the role name.
     * 
     * @return The role name
     */
    public RoleName getName() {
        return name;
    }

    // =======
    // Setters
    // =======

    /**
     * Sets the role ID.
     * 
     * @param id The ID assigned to the role.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the role name.
     * 
     * @param name The role name assigned.
     */
    public void setName(RoleName name) {
        this.name = name;
    }
    
}