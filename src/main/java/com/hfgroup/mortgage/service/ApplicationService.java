package com.hfgroup.mortgage.service;

import com.hfgroup.mortgage.Repository.ApplicationRepository;
import com.hfgroup.mortgage.Repository.DecisionRepository;
import com.hfgroup.mortgage.dto.request.ApplicationDTO;
import com.hfgroup.mortgage.dto.request.ApplicationFilterDTO;
import com.hfgroup.mortgage.dto.request.DecisionDTO;
import com.hfgroup.mortgage.exception.ApplicationNotFoundException;
import com.hfgroup.mortgage.exception.DecisionAlreadyExistsException;
import com.hfgroup.mortgage.model.Application;
import com.hfgroup.mortgage.model.Decision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DecisionRepository decisionRepository;

    public ApplicationService(ApplicationRepository applicationRepository, DecisionRepository decisionRepository) {
        this.applicationRepository = applicationRepository;
        this.decisionRepository = decisionRepository;
    }

    /**
     * Method to save a new application in the database.
     * @param applicationDTO DTO received from the controller.
     * @return The saved Application entity.
     */
    public Application createApplication(ApplicationDTO applicationDTO) {
        // Convert DTO to Entity
        Application application = new Application();
        application.setApplicantId(applicationDTO.getApplicantId());
        application.setNationalId(applicationDTO.getNationalId());
        application.setStatus(applicationDTO.getStatus() != null ? applicationDTO.getStatus() : "PENDING");
        application.setAmount(applicationDTO.getAmount() != null ? applicationDTO.getAmount() : 0.0);

        return applicationRepository.save(application);
    }

    /**
     * Method to get an application by ID.
     * @param id The application ID.
     * @return Optional containing the application if found.
     */
    public Optional<Application> getApplicationById(UUID id) {
        return applicationRepository.findById(id);
    }

    /**
     * Method to get applications with filters.
     * @param filterDTO The filter criteria.
     * @return Page of applications matching the criteria.
     */
    public Page<Application> getApplicationsWithFilters(ApplicationFilterDTO filterDTO) {
        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize());
        
        return applicationRepository.findApplicationsWithFilters(
                filterDTO.getStatus(),
                filterDTO.getNationalId(),
                filterDTO.getCreatedFrom(),
                filterDTO.getCreatedTo(),
                pageable
        );
    }

    /**
     * Method to create a decision for an application.
     * @param applicationId The application ID.
     * @param decisionDTO The decision details.
     * @return The created decision.
     */
    @Transactional
    public Decision createDecision(UUID applicationId, DecisionDTO decisionDTO) {
        // Check if application exists
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with ID: " + applicationId));

        // Check if decision already exists for this application
        if (decisionRepository.findByApplicationId(applicationId).isPresent()) {
            throw new DecisionAlreadyExistsException("Decision already exists for application: " + applicationId);
        }

        // Validate decision status
        if (!"APPROVED".equals(decisionDTO.getDecision()) && !"REJECTED".equals(decisionDTO.getDecision())) {
            throw new RuntimeException("Decision must be either 'APPROVED' or 'REJECTED'");
        }

        // Create decision
        Decision decision = Decision.builder()
                .applicationId(applicationId)
                .approverId(decisionDTO.getApproverId())
                .decision(decisionDTO.getDecision())
                .comment(decisionDTO.getComment())
                .build();

        Decision savedDecision = decisionRepository.save(decision);

        // Update application status
        application.setStatus(decisionDTO.getDecision());
        applicationRepository.save(application);

        return savedDecision;
    }
}