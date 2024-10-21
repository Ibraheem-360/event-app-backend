package com.event.management.event_management.repository;

import com.event.management.event_management.entity.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
    List<Attendee> findByEventId(Long eventId);  // Find attendees for a specific event
    Optional<Attendee> findByUserIdAndEventId(Long userId, Long eventId);  // Check if user already registered
}
