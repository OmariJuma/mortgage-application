package com.hfgroup.mortgage.Repository;

import com.hfgroup.mortgage.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
}
