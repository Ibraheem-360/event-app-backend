package com.event.management.event_management.service;

import com.event.management.event_management.dto.RegisterRequest;
import com.event.management.event_management.entity.Role;
import com.event.management.event_management.entity.User;
import com.event.management.event_management.exception.UserAlreadyExistsException;
import com.event.management.event_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user with default ROLE_USER
    @Override
    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsernameOrEmail(
                registerRequest.getUsername(), registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Username or email is already taken.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ROLE_USER);  // Default to USER role

        return userRepository.save(user);
    }

    // Register the first admin (only if no admin exists yet)
    public User registerFirstAdmin(RegisterRequest registerRequest) {
        if (userRepository.existsByRole(Role.ROLE_ADMIN)) {
            throw new UserAlreadyExistsException("First admin already exists.");
        }

        User admin = new User();
        admin.setUsername(registerRequest.getUsername());
        admin.setEmail(registerRequest.getEmail());
        admin.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        admin.setRole(Role.ROLE_ADMIN);  // Assign admin role

        return userRepository.save(admin);
    }

    // Register additional admins (admin privilege required)
    public User registerAdmin(RegisterRequest registerRequest) {
        if (userRepository.existsByUsernameOrEmail(
                registerRequest.getUsername(), registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Username or email is already taken.");
        }

        User admin = new User();
        admin.setUsername(registerRequest.getUsername());
        admin.setEmail(registerRequest.getEmail());
        admin.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        admin.setRole(Role.ROLE_ADMIN);

        return userRepository.save(admin);
    }

    // Find a user by their username
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Check if an admin is already registered
    public boolean isFirstAdminRegistered() {
        return userRepository.existsByRole(Role.ROLE_ADMIN);
    }

    // Fetch all users (for admin purposes)
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
