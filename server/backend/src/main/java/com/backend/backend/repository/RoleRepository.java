package com.backend.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.backend.model.Role;
import com.backend.backend.model.RoleName;

/**
 * The RoleRepository interface will handle CRUD operations for the User entity.
 * 
 * It extends JpaRepository which is what provides built in CRUD operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

    /**
     * Finds a role by its name
     * 
     * @param roleName The name of the role
     * 
     * @return The role if found, otherwise empty
     */
    Optional<Role> findByName (RoleName roleName);
}