package com.event.management.event_management.controller;

import com.event.management.event_management.dto.AttendeeDTO;
import com.event.management.event_management.entity.Attendee;
import com.event.management.event_management.repository.UserRepository;
import com.event.management.event_management.security.JwtTokenProvider;
import com.event.management.event_management.service.AttendeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendees")
public class AttendeeController {

    private final AttendeeService attendeeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AttendeeController(AttendeeService attendeeService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.attendeeService = attendeeService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * Register the logged-in user (admin or user) for an event.
     */
    @PostMapping("/register/{eventId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AttendeeDTO> registerForEvent(
            @PathVariable Long eventId, Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);  // Extract user ID
        Attendee attendee = attendeeService.registerForEvent(eventId, userId);

        // Constructing a more informative response DTO
        AttendeeDTO responseDTO = new AttendeeDTO(
                attendee.getId(),
                attendee.getUser().getId(),
                attendee.getUser().getUsername(),  // Add username
                attendee.getEvent().getId(),
                attendee.getEvent().getTitle()  // Add event title
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    /**
     * Cancel the registration for an event.
     */
    @DeleteMapping("/cancel/{attendeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")  // Both can cancel registrations
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long attendeeId) {
        attendeeService.cancelRegistration(attendeeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all attendees for a specific event.
     */
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<AttendeeDTO>> getAttendeesByEvent(@PathVariable Long eventId) {
        List<AttendeeDTO> attendees = attendeeService.getAttendeesByEvent(eventId);
        return ResponseEntity.ok(attendees);
    }

    /**
     * Helper method to extract the user ID from the Authentication object.
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication is missing or invalid");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            // Use the username to fetch the user from the database
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username))
                    .getId();
        }

        throw new IllegalStateException("Unexpected authentication structure");
    }




}
