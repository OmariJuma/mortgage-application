package com.hfgroup.mortgage.Repository;

import com.hfgroup.mortgage.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    // Find by applicant ID
    @EntityGraph(attributePaths = "documents")
    Optional<Application> findById(UUID id);
    // Find by status
    Page<Application> findByStatus(String status, Pageable pageable);
    
    // Find by national ID
    Page<Application> findByNationalId(String nationalId, Pageable pageable);
    
    // Find by date range
    Page<Application> findByCreatedAtBetween(LocalDateTime createdFrom, LocalDateTime createdTo, Pageable pageable);
    
    // Find by status and date range
    Page<Application> findByStatusAndCreatedAtBetween(String status, LocalDateTime createdFrom, LocalDateTime createdTo, Pageable pageable);
    
    // Find by national ID and date range
    Page<Application> findByNationalIdAndCreatedAtBetween(String nationalId, LocalDateTime createdFrom, LocalDateTime createdTo, Pageable pageable);
    
    // Find by status and national ID
    Page<Application> findByStatusAndNationalId(String status, String nationalId, Pageable pageable);
    
    // Find by status, national ID and date range
    Page<Application> findByStatusAndNationalIdAndCreatedAtBetween(String status, String nationalId, LocalDateTime createdFrom, LocalDateTime createdTo, Pageable pageable);
}
