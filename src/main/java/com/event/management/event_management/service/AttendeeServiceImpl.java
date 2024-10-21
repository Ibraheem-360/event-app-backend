package com.event.management.event_management.service;

import com.event.management.event_management.dto.AttendeeDTO;
import com.event.management.event_management.entity.Attendee;
import com.event.management.event_management.entity.Event;
import com.event.management.event_management.entity.User;
import com.event.management.event_management.exception.ResourceNotFoundException;
import com.event.management.event_management.repository.AttendeeRepository;
import com.event.management.event_management.repository.EventRepository;
import com.event.management.event_management.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendeeServiceImpl implements AttendeeService {

    private final AttendeeRepository attendeeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public AttendeeServiceImpl(AttendeeRepository attendeeRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.attendeeRepository = attendeeRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Attendee registerForEvent(Long eventId, Long userId) {
        // Ensure the user and event exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Check if the user is already registered for the event
        attendeeRepository.findByUserIdAndEventId(userId, eventId).ifPresent(attendee -> {
            throw new IllegalStateException("User is already registered for this event");
        });

        // Create and save new attendee
        Attendee attendee = new Attendee();
        attendee.setUser(user);
        attendee.setEvent(event);

        return attendeeRepository.save(attendee);
    }

    @Override
    public void cancelRegistration(Long attendeeId) {
        Attendee attendee = attendeeRepository.findById(attendeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendee not found"));
        attendeeRepository.delete(attendee);
    }

    @Override
    public List<AttendeeDTO> getAttendeesByEvent(Long eventId) {
        List<Attendee> attendees = attendeeRepository.findByEventId(eventId);
        return attendees.stream().map(attendee -> new AttendeeDTO(
                attendee.getId(),
                attendee.getUser().getId(),
                attendee.getUser().getUsername(),  // Include username
                attendee.getEvent().getId(),
                attendee.getEvent().getTitle()  // Include event title
        )).collect(Collectors.toList());
    }

}
