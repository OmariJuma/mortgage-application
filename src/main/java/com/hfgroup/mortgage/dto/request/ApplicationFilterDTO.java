package com.hfgroup.mortgage.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFilterDTO {
    private String status;
    private String nationalId;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private Integer page = 0;
    private Integer size = 10;
} 