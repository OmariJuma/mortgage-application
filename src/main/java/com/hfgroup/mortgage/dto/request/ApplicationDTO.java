package com.hfgroup.mortgage.dto.request;

import lombok.*;

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
    private Double amount;

    private List<DocumentMetadata> documents;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentMetadata {
        private String fileName;
        private String filePath;
        private String documentType;
        private String presignedUrl;
        private long size;
        private String fileType;
    }
}