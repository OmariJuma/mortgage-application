package com.hfgroup.mortgage.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private UUID id;
    private UUID applicantId;
    private String nationalId;
    private String status;
    private List<UUID> documentIds;
}
