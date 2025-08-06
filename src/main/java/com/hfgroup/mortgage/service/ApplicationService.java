package com.hfgroup.mortgage.service;

import com.hfgroup.mortgage.Repository.ApplicationRepository;
import com.hfgroup.mortgage.dto.request.ApplicationDTO;
import com.hfgroup.mortgage.model.Application;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Method to save a new application in the database.
     * @param applicationDTO DTO received from the controller.
     * @return The saved Application entity.
     */
    public Application createApplication(ApplicationDTO applicationDTO) {
        // Convert DTO to Entity
        Application application = new Application();
        application.setApplicantId(applicationDTO.getApplicantId()); // Fetch and set the actual User entity by ID if applicable
        application.setNationalId(applicationDTO.getNationalId());
        application.setStatus(applicationDTO.getStatus());
        application.setAmount(application.getAmount());
        application.setUpdatedAt(null);

        return applicationRepository.save(application);
    }
}