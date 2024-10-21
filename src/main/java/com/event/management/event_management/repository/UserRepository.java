package com.event.management.event_management.repository;

import com.event.management.event_management.entity.Role;
import com.event.management.event_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByRole(Role role);  // Check if any admin exists

    Optional<User> findByUsername(String username);
}
