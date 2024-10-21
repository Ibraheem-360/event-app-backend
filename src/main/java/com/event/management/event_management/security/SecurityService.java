package com.event.management.event_management.security;

import com.event.management.event_management.entity.Event;
import com.event.management.event_management.repository.EventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final EventRepository eventRepository;

    public SecurityService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public boolean isEventCreator(Long eventId, String username) {
        return eventRepository.findById(eventId)
                .map(Event::getCreator)
                .map(creator -> creator.getUsername().equals(username))
                .orElse(false);
    }
}
