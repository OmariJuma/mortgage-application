package com.hfgroup.mortgage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String roles;
}
