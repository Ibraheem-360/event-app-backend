package com.event.management.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private Integer capacity;

    private CreatorDTO creator;  // Only essential fields of the creator

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatorDTO {
        private Long id;
        private String username;
    }
}
