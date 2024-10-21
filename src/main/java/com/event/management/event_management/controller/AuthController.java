package com.event.management.event_management.controller;

import com.event.management.event_management.dto.LoginRequest;
import com.event.management.event_management.dto.RegisterRequest;
import com.event.management.event_management.entity.User;
import com.event.management.event_management.exception.UserAlreadyExistsException;
import com.event.management.event_management.security.JwtTokenProvider;
import com.event.management.event_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Value("${admin.registration.token}")
    private String FIRST_ADMIN_TOKEN;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    // Register a regular user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User registeredUser = userService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", registeredUser
            ));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // Login endpoint to authenticate and generate JWT token
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Fetch user details from the database
            User user = userService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Generate JWT token including userId
            String token = jwtTokenProvider.generateToken(authentication, user.getId());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "token", token
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid username or password"
            ));
        }
    }

    // Register the first admin with a predefined token
    @PostMapping("/register-first-admin")
    public ResponseEntity<?> registerFirstAdmin(
            @Valid @RequestBody RegisterRequest registerRequest,
            @RequestParam("token") String token) {

        // Validate the admin registration token
        if (!FIRST_ADMIN_TOKEN.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "message", "Invalid token for first admin registration"
            ));
        }

        // Check if the first admin already exists
        if (userService.isFirstAdminRegistered()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "First admin has already been registered"
            ));
        }

        try {
            User admin = userService.registerFirstAdmin(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", admin
            ));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // Register a new admin (requires admin privileges)
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @Valid @RequestBody RegisterRequest registerRequest,
            Authentication authentication) {

        // Ensure the user is authenticated and has admin privileges
        if (authentication == null || authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "message", "Access denied. Admin privileges required"
            ));
        }

        try {
            User admin = userService.registerAdmin(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", admin
            ));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
