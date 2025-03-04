package com.backend.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.backend.model.User;

/**
 * The UserRepository interface will handle CRUD operations for the User entity.
 * 
 * It extends JpaRepository which is what provides built in CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    /**
     * Finds a list of users by their ID's
     * 
     * @param userIds A list of user ID's to search for
     * 
     * @return A list of users whose ID's match those in the list
     */
    List<User> findByIdIn (List<Long> userIds);

    /**
     * Find a user by email
     * 
     * @param email The email of the user to find
     * 
     * @return The user if found or empty if no match exists
     */
    Optional<User> findByEmail (String email);

    /**
     * Find user by username
     * 
     * @param username The username of the user to find
     * 
     * @return The user if found or empty if no match exists
     */
    Optional<User> findByUsername (String username);
    
    /**
     * Find user by username or email
     * 
     * @param username The username of the user to find
     * 
     * @param email The email of the user to find
     * 
     * @return The user if found or empty if no match exists
     */
    Optional<User> findByUsernameOrEmail (String username, String email);

    /**
     * A check if the user exists with given username
     * 
     * @param username The username to check
     * 
     * @return True if username exists, false otherwise
     */
    Boolean existsByUsername (String username);

    /**
     * A check if the user exists with given email
     * 
     * @param email The email to check
     * 
     * @return True if email exists, false otherwise
     */
    Boolean existsByEmail (String email);
}