package com.event.management.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeDTO {
    private Long id;
    private Long userId;
    private String username;  // Optional: User's name
    private Long eventId;
    private String eventTitle;  // Optional: Event title
}
