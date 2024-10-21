package com.event.management.event_management.controller;

import com.event.management.event_management.dto.EventDTO;
import com.event.management.event_management.entity.Event;
import com.event.management.event_management.exception.ResourceNotFoundException;
import com.event.management.event_management.repository.UserRepository;
import com.event.management.event_management.security.JwtTokenProvider;
import com.event.management.event_management.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;  // For extracting user ID from JWT token

    public EventController(EventService eventService, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Create a new event. Both admin and users can create events.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody EventDTO eventDTO, Authentication authentication) {

        String username = authentication.getName();  // Extract username from token
        Event createdEvent = eventService.createEvent(eventDTO, username);

        // Convert Event to EventDTO to prevent exposing unnecessary data.
        EventDTO responseDTO = mapToDTO(createdEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Update an existing event. Only the event creator or an admin can update.
     */
    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventDTO eventDTO,
            Authentication authentication) {

        try {
            String username = authentication.getName();  // Extract username from token

            // Call the service layer to update the event
            Event updatedEvent = eventService.updateEvent(eventId, eventDTO, username);

            // Convert to DTO to avoid exposing internal data
            EventDTO responseDTO = mapToDTO(updatedEvent);

            return ResponseEntity.ok(responseDTO);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    /**
     * Delete an event. Only admins can delete events.
     */
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all events (open to all users).
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    /**
     * Get events created by a specific user.
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<EventDTO>> getEventsByCreator(@PathVariable Long creatorId) {
        List<EventDTO> events = eventService.getEventsByCreator(creatorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    /**
     * Get events the current user is registered for.
     */
    @GetMapping("/my-registered")
    public ResponseEntity<List<EventDTO>> getMyRegisteredEvents(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<EventDTO> events = eventService.getMyRegisteredEvents(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    /**
     * Helper method to extract the user ID from Authentication.
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


    /**
     * Helper method to map Event to EventDTO.
     */
    private EventDTO mapToDTO(Event event) {
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getEventDate(),
                event.getCapacity(),
                new EventDTO.CreatorDTO(
                        event.getCreator().getId(),
                        event.getCreator().getUsername()
                )
        );
    }
}
