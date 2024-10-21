package com.event.management.event_management.dto;

import com.event.management.event_management.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;  // Role can be ADMIN or USER
}
