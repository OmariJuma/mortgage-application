package com.hfgroup.mortgage.service;

import com.hfgroup.mortgage.Repository.ApplicationRepository;
import com.hfgroup.mortgage.Repository.DecisionRepository;
import com.hfgroup.mortgage.Repository.DocumentRepository;
import com.hfgroup.mortgage.Repository.UserRepository;
import com.hfgroup.mortgage.dto.request.ApplicationDTO;
import com.hfgroup.mortgage.dto.request.ApplicationFilterDTO;
import com.hfgroup.mortgage.dto.request.DecisionDTO;
import com.hfgroup.mortgage.exception.ApplicationNotFoundException;
import com.hfgroup.mortgage.exception.DecisionAlreadyExistsException;
import com.hfgroup.mortgage.model.Application;
import com.hfgroup.mortgage.model.Decision;
import com.hfgroup.mortgage.model.Document;
import com.hfgroup.mortgage.security.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DecisionRepository decisionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final KafkaProducerService kafkaProducerService;

    public ApplicationService(ApplicationRepository applicationRepository, DecisionRepository decisionRepository, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, DocumentRepository documentRepository, KafkaProducerService kafkaProducerService) {
        this.applicationRepository = applicationRepository;
        this.decisionRepository = decisionRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    private <T> Object buildEventPayload(String eventType, T data) {
        return new Object() {
            public final String event = eventType;  // "CREATE", "UPDATE", "DELETE"
            public final String traceId = UUID.randomUUID().toString();
            public final String version = "1.0";
            public final String timestamp = LocalDateTime.now().toString();
            public final T payload = data;
        };
    }

    /**
     * Method to save a new application in the database.
     * @param applicationDTO DTO received from the controller.
     * @return The saved Application entity.
     */
    public Application createApplication(ApplicationDTO applicationDTO) {
        // Save the application
        Application application = new Application();
        application.setApplicantId(applicationDTO.getApplicantId());
        application.setNationalId(applicationDTO.getNationalId());
        application.setAmount(applicationDTO.getAmount());
        application.setStatus(applicationDTO.getStatus() != null ? applicationDTO.getStatus() : "PENDING");

        Application savedApplication = applicationRepository.save(application);

        // Map and save the documents
        if (applicationDTO.getDocuments() != null) {
            List<Document> documents = applicationDTO.getDocuments().stream().map(documentDTO -> {
                Document document = new Document();
                document.setApplication(savedApplication);
                document.setFileName(documentDTO.getFileName());
                document.setUrl(documentDTO.getPresignedUrl());
                document.setFileType(documentDTO.getFileType());
                document.setSize(documentDTO.getSize());

                return document;
            }).collect(Collectors.toList());

            documentRepository.saveAll(documents);
            savedApplication.setDocuments(documents);
        }
        kafkaProducerService.publishMessage(
                "loan.applications",
                savedApplication.getId(),
                buildEventPayload("CREATE", savedApplication)
        );
        return savedApplication;
    }

    /**
     * Method to get an application by ID.
     * @param id The application ID.
     * @return Optional containing the application if found.
     */
    public Optional<Application> getApplicationById(UUID id) {
        Optional<Application> application = applicationRepository.findById(id);
        kafkaProducerService.publishMessage(
                "loan.applications",
                application.get().getId(),
                buildEventPayload("UPDATE", application)
        );
        return application;
    }

    /**
     * Method to get applications with filters.
     * @param filterDTO The filter criteria.
     * @return Page of applications matching the criteria.
     */
    public Page<Application> getApplicationsWithFilters(ApplicationFilterDTO filterDTO) {
        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize());
        
        String status = filterDTO.getStatus();
        String nationalId = filterDTO.getNationalId();
        LocalDateTime createdFrom = filterDTO.getCreatedFrom();
        LocalDateTime createdTo = filterDTO.getCreatedTo();

        // Determine which repository method to use based on provided filters
        if (status != null && nationalId != null && createdFrom != null && createdTo != null) {
            return applicationRepository.findByStatusAndNationalIdAndCreatedAtBetween(status, nationalId, createdFrom, createdTo, pageable);
        } else if (status != null && nationalId != null) {
            return applicationRepository.findByStatusAndNationalId(status, nationalId, pageable);
        } else if (status != null && createdFrom != null && createdTo != null) {
            return applicationRepository.findByStatusAndCreatedAtBetween(status, createdFrom, createdTo, pageable);
        } else if (nationalId != null && createdFrom != null && createdTo != null) {
            return applicationRepository.findByNationalIdAndCreatedAtBetween(nationalId, createdFrom, createdTo, pageable);
        } else if (status != null) {
            return applicationRepository.findByStatus(status, pageable);
        } else if (nationalId != null) {
            return applicationRepository.findByNationalId(nationalId, pageable);
        } else if (createdFrom != null && createdTo != null) {
            return applicationRepository.findByCreatedAtBetween(createdFrom, createdTo, pageable);
        } else {
            // No filters provided, return all applications

            return applicationRepository.findAll(pageable);
        }
    }
    
    /**
     * Method to get all applications without filters.
     * @param page Page number.
     * @param size Page size.
     * @return Page of all applications.
     */
    public Page<Application> getAllApplications(Integer page, Integer size) {
        Page<Application> applications = applicationRepository.findAll(PageRequest.of(page, size));
        return applications;
    }

    /**
     * Method to create a decision for an application.
     * @param applicationId The application ID.
     * @param decisionDTO The decision details.
     * @param authorizationHeader The authorization header containing the JWT token.
     * @return The created decision.
     */
    @Transactional
    public Decision createDecision(UUID applicationId, DecisionDTO decisionDTO, String authorizationHeader) {
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
        String username = jwtTokenProvider.getUsernameFromToken(authorizationHeader.replace("Bearer ", ""));
       UUID  approverId = userRepository.findByUsername(username).get().getId();
        // Create decision
        Decision decision = Decision.builder()
                .applicationId(applicationId)
                .approverId(approverId)
                .decision(decisionDTO.getDecision())
                .comment(decisionDTO.getComment())
                .build();

        Decision savedDecision = decisionRepository.save(decision);

        // Update application status
        application.setStatus(decisionDTO.getDecision());
        applicationRepository.save(application);
        kafkaProducerService.publishMessage(
                "loan.applications",
                savedDecision.getId(),
                buildEventPayload("UPDATE", savedDecision)
        );        return savedDecision;
    }
}