package com.event.management.event_management.service;

import com.event.management.event_management.dto.EventDTO;
import com.event.management.event_management.entity.Event;

import java.util.List;

public interface EventService {
    Event createEvent(EventDTO eventDTO, String username);  // Align parameter name

    Event updateEvent(Long eventId, EventDTO eventDTO, String username);  // Use username consistently
    // Include isAdmin flag

    void deleteEvent(Long eventId);  // Admin only

    List<Event> getAllEvents();  // Open to all

    List<Event> getEventsByCreator(Long creatorId);  // For event creators

    List<Event> getMyRegisteredEvents(Long userId);  // For users
}
