package com.backend.backend.model.audit;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

/**
 * The DateAudit class will provide date and times of when an entity was created or updated.
 * These times are automatically captured and maintained.
 * 
 * The class can be extended to any entity class that might require tracking of creation or modification.
 */
@MappedSuperclass   // This class is a base class and it's fields will be inherited by classes extended to it
@EntityListeners(AuditingEntityListener.class)  // Enables automatic time stamping
@JsonIgnoreProperties(
    value = {
        "createdAt",
        "updatedAt"
    },
    allowGetters = true
)
public abstract class DateAudit implements Serializable{
    // ===============
    // Database Fields
    // ===============

    // Timestamp when entity is created
    @CreatedDate
    @Column(
        nullable = false,   // Ensures field is not null
        updatable = false   // Prevents modification after creation
    )
    private Instant createdAt;

    // Timestamp when the entity was last updated
    @LastModifiedDate
    @Column(nullable = false)   // Ensures field is not null
    private Instant updatedAt;

    // =======
    // Getters
    // =======

    /**
     * Gets the creation timestamp
     * 
     * @return The timestamp when the entity was created
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the updated timestamp
     * 
     * @return The timestamp when the entity was last updated
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // =======
    // Setters
    // =======

    /**
     * Sets the creation timestamp
     * 
     * @param createdAt The timestamp assigned at creation
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Sets the last updated timestamp
     * 
     * @param updatedAt The timestamp assigned at last update
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}