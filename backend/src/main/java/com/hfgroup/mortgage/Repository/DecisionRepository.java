package com.hfgroup.mortgage.Repository;

import com.hfgroup.mortgage.model.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DecisionRepository extends JpaRepository<Decision, UUID> {
    Optional<Decision> findByApplicationId(UUID applicationId);
} 