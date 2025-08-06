package com.hfgroup.mortgage.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDTO {
    private String decision; // APPROVED or REJECTED
    private UUID approverId;
    private String comment;
} 