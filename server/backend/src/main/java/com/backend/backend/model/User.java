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
    // ===============
    // Database Fields
    // ===============

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

    /**
     * Default constructor that is required for JPA.
     */
    public User() {

    }

    /**
     * Constructor to initalize a new User object with specific parameters
     * 
     * @param name The user's name
     * @param username The user's chosen username
     * @param email The user's email
     * @param password The user's password
     */
    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // =======
    // Getters
    // =======

    /**
     * Gets the user's ID.
     * 
     * @return The ID of the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the user's name.
     * 
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's username.
     * 
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the user's email.
     * 
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's password.
     * 
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user's role(s)
     * 
     * @return The role(s) of the user.
     */
    public Set<Role> getRoles() {
        return roles;
    }

    // =======
    // Setters
    // =======

    /**
     * Sets the user's ID.
     * 
     * @param id The ID assigned to the user.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the user's name.
     * 
     * @param name The name assigned to the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the user's username.
     * 
     * @param username The username assigned to the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the user's email
     * 
     * @param email The email assigned to the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's password.
     * 
     * @param password The password assigned to the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the users role(s).
     * 
     * @param roles The role(s) assigned to the user.
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
}