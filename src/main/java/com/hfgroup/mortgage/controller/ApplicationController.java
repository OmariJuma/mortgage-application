package com.hfgroup.mortgage.controller;

import com.hfgroup.mortgage.dto.request.ApplicationDTO;
import com.hfgroup.mortgage.dto.request.ApplicationFilterDTO;
import com.hfgroup.mortgage.dto.request.DecisionDTO;
import com.hfgroup.mortgage.model.Application;
import com.hfgroup.mortgage.model.Decision;
import com.hfgroup.mortgage.service.ApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public Application createApplication(@RequestBody ApplicationDTO applicationDTO) {
        return applicationService.createApplication(applicationDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable UUID id) {
        return applicationService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Application>> getApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        ApplicationFilterDTO filterDTO = ApplicationFilterDTO.builder()
                .status(status)
                .nationalId(nationalId)
                .createdFrom(createdFrom != null ? java.time.LocalDateTime.parse(createdFrom) : null)
                .createdTo(createdTo != null ? java.time.LocalDateTime.parse(createdTo) : null)
                .page(page)
                .size(size)
                .build();
        
        Page<Application> applications = applicationService.getApplicationsWithFilters(filterDTO);
        return ResponseEntity.ok(applications);
    }

    @PatchMapping("/{id}/decision")
    public ResponseEntity<Decision> createDecision(
            @PathVariable UUID id,
            @RequestBody DecisionDTO decisionDTO) {
        Decision decision = applicationService.createDecision(id, decisionDTO);
        return ResponseEntity.ok(decision);
    }
}
