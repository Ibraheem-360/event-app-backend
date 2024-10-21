package com.event.management.event_management.service;

import com.event.management.event_management.dto.EventDTO;
import com.event.management.event_management.entity.Event;
import com.event.management.event_management.entity.User;
import com.event.management.event_management.exception.ResourceNotFoundException;
import com.event.management.event_management.repository.EventRepository;
import com.event.management.event_management.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new event with the currently logged-in user as the creator.
     */
    @Override
    public Event createEvent(EventDTO eventDTO, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setLocation(eventDTO.getLocation());
        event.setEventDate(eventDTO.getEventDate());
        event.setCapacity(eventDTO.getCapacity());
        event.setCreator(creator);  // Set the creator as the logged-in user

        return eventRepository.save(event);
    }

    /**
     * Update an existing event. The creator can only update their own events. Admins can update any event.
     */
    @Override
    public Event updateEvent(Long eventId, EventDTO eventDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        // Only allow the creator or an admin to update the event
        if (!event.getCreator().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("You are not authorized to update this event.");
        }

        // Update event details
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setLocation(eventDTO.getLocation());
        event.setEventDate(eventDTO.getEventDate());
        event.setCapacity(eventDTO.getCapacity());

        return eventRepository.save(event);
    }


    /**
     * Delete an event. Only admins are allowed to delete events.
     */
    @Override
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        eventRepository.delete(event);
    }

    /**
     * Get all events. This is available to all users.
     */
    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Get events created by a specific user (for event creators).
     */
    @Override
    public List<Event> getEventsByCreator(Long creatorId) {
        return eventRepository.findByCreatorId(creatorId);
    }

    /**
     * Get events that the current user is registered for.
     */
    @Override
    public List<Event> getMyRegisteredEvents(Long userId) {
        return eventRepository.findEventsByUserId(userId);
    }
}
