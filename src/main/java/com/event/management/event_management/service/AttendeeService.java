package com.event.management.event_management.service;

import com.event.management.event_management.dto.AttendeeDTO;
import com.event.management.event_management.entity.Attendee;

import java.util.List;

public interface AttendeeService {
    Attendee registerForEvent(Long eventId, Long userId);  // Register user for event
    void cancelRegistration(Long attendeeId);  // Cancel registration
    List<AttendeeDTO> getAttendeesByEvent(Long eventId);  // List attendees of an event
}
