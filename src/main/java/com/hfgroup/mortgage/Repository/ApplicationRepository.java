package com.hfgroup.mortgage.Repository;

import com.hfgroup.mortgage.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    
    @Query("SELECT a FROM Application a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:nationalId IS NULL OR a.nationalId = :nationalId) AND " +
           "(:createdFrom IS NULL OR a.createdAt >= :createdFrom) AND " +
           "(:createdTo IS NULL OR a.createdAt <= :createdTo)")
    Page<Application> findApplicationsWithFilters(
            @Param("status") String status,
            @Param("nationalId") String nationalId,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            Pageable pageable);
}
