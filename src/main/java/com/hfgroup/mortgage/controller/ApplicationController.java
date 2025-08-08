package com.hfgroup.mortgage.controller;

import com.hfgroup.mortgage.dto.request.ApplicationDTO;
import com.hfgroup.mortgage.dto.request.ApplicationFilterDTO;
import com.hfgroup.mortgage.dto.request.DecisionDTO;
import com.hfgroup.mortgage.model.Application;
import com.hfgroup.mortgage.model.Decision;
import com.hfgroup.mortgage.service.ApplicationService;
import com.hfgroup.mortgage.service.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {
    private final ApplicationService applicationService;
    private final S3Service s3Service;

    public ApplicationController(ApplicationService applicationService, S3Service s3Service) {
        this.applicationService = applicationService;
        this.s3Service = s3Service;
    }

    @PostMapping
    @PreAuthorize("hasRole('APPLICANT')")
    public Application createApplication(@RequestBody ApplicationDTO applicationDTO) {
        // Loop through documents and handle S3 uploads and presigned URL generation
        List<ApplicationDTO.DocumentMetadata> updatedMetadata = applicationDTO.getDocuments().stream().map(document -> {
            String bucketName = "oj-mortgage-application-documents";
            String keyName = "mortgage-applications/" + document.getFileName();

            s3Service.uploadFile(bucketName, keyName, document.getFilePath());

            URL presignedUrl = s3Service.generatePresignedUrl(bucketName, keyName, Duration.ofDays(7));

            return ApplicationDTO.DocumentMetadata.builder()
                    .fileName(document.getFileName())
                    .filePath(keyName)
                    .documentType(document.getDocumentType())
                    .fileType(document.getFileType())
                    .presignedUrl(presignedUrl.toString())
                    .build();
        }).collect(Collectors.toList());

        applicationDTO.setDocuments(updatedMetadata);
        return applicationService.createApplication(applicationDTO);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'OFFICER')")
    public ResponseEntity<Application> getApplicationById(@PathVariable UUID id) {
        return applicationService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'OFFICER')")
    public ResponseEntity<Page<Application>> getApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;
        
        // Parse date parameters with proper error handling
        if (createdFrom != null && !createdFrom.trim().isEmpty()) {
            try {
                // Try to parse as LocalDateTime first
                fromDate = LocalDateTime.parse(createdFrom, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                try {
                    // If that fails, try to parse as LocalDate and convert to start of day
                    fromDate = java.time.LocalDate.parse(createdFrom, DateTimeFormatter.ISO_LOCAL_DATE)
                            .atStartOfDay();
                } catch (DateTimeParseException e2) {
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        
        if (createdTo != null && !createdTo.trim().isEmpty()) {
            try {
                // Try to parse as LocalDateTime first
                toDate = LocalDateTime.parse(createdTo, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                try {
                    // If that fails, try to parse as LocalDate and convert to end of day
                    toDate = java.time.LocalDate.parse(createdTo, DateTimeFormatter.ISO_LOCAL_DATE)
                            .atTime(23, 59, 59, 999999999);
                } catch (DateTimeParseException e2) {
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        
        ApplicationFilterDTO filterDTO = ApplicationFilterDTO.builder()
                .status(status)
                .nationalId(nationalId)
                .createdFrom(fromDate)
                .createdTo(toDate)
                .page(page)
                .size(size)
                .build();
        
        Page<Application> applications = applicationService.getApplicationsWithFilters(filterDTO);
        return ResponseEntity.ok(applications);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<Page<Application>> getAllApplications(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Page<Application> applications = applicationService.getAllApplications(page, size);
        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/{id}/decision")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<Decision> createDecision(
            @PathVariable UUID id,
            @RequestBody DecisionDTO decisionDTO,
            @RequestHeader("Authorization") String authorizationHeader
            ) {
        Decision decision = applicationService.createDecision(id, decisionDTO, authorizationHeader);
        return ResponseEntity.ok(decision);
    }
}
