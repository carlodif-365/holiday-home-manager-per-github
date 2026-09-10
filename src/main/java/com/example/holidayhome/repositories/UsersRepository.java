package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.Role;
import com.example.holidayhome.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    // JPQL
    @Query("SELECT u FROM User u WHERE u.firstName = :name OR u.lastName = :name")
    List<User> searchByName(String name);

    // Native query
    @Query(value = "SELECT COUNT(*) FROM users WHERE role = :role", nativeQuery = true)
    long countByRoleNative(String role);
}
