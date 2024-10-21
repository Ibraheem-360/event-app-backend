package com.event.management.event_management.repository;

import com.event.management.event_management.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatorId(Long creatorId);

    @Query("SELECT e FROM Event e JOIN Attendee a ON e.id = a.event.id WHERE a.user.id = :userId")
    List<Event> findEventsByUserId(Long userId);
}
