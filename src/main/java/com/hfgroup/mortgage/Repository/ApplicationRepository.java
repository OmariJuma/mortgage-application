package com.hfgroup.mortgage.Repository;

import com.hfgroup.mortgage.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
}
