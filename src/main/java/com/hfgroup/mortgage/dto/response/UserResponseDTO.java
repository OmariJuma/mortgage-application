package com.hfgroup.mortgage.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDTO {
    
    private UUID id;
    private String username;
    private String roles;
    private LocalDateTime createdAt;
}
