package com.backend.backend.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * The User class represents a user entity in the database.
 * It contains user details such as name, username, email, password, and the users role.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    // Makes sure that username is UNIQUE in the table
    @UniqueConstraint(columnNames = {
        "username"
    }),
    // Makse sure that email is UNIQUE in the table
    @UniqueConstraint(columnNames = {
        "email"
    })
})
public class User {
    // ================
    // Database Columns
    // ================

    // Field 'id' is a PRIMARY KEY and the id auto-increments in the database
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Field 'name' is not null or empty and is limited to 32 characters
    @NotBlank
    @Size(max = 32)
    private String name;

    // Field 'username' is not null or empty and is limited to 16 characters
    @NotBlank
    @Size(max = 16)
    private String username;

    // Field 'email' is has NaturalId used for searching, is not null or empty, limitied to 64 characters, and must be a valid email
    @NaturalId
    @NotBlank
    @Size(max = 64)
    @Email
    private String email;

    // Field 'password' is not null or empty and is limited to 128 characters
    @NotBlank
    @Size(max = 128)
    private String password;

    // =============
    // Relationships
    // =============

    // Many-to-Many relationship with Role entity that initializes an empty set of roles
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", // This is the specified join table
                joinColumns = @JoinColumn(name = "user_id"),        // This is the foreign key for User entity
                inverseJoinColumns = @JoinColumn(name = "role_id")) // This is the foreign key for Role entity
    private Set<Role> roles = new HashSet<>();

    // ============
    // Constructors
    // ============

    // Default constructor that is required for JPA
    public User() {

    }

    // Constructor to initalize a new User object
    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // =======
    // Getters
    // =======

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    // =======
    // Setters
    // =======

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
}