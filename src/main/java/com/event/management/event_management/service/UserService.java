package com.event.management.event_management.service;

import com.event.management.event_management.dto.RegisterRequest;
import com.event.management.event_management.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(RegisterRequest registerRequest);

    User registerFirstAdmin(RegisterRequest registerRequest);

    User registerAdmin(RegisterRequest registerRequest);

    Optional<User> findByUsername(String username);

    boolean isFirstAdminRegistered();

    List<User> getAllUsers();
}
