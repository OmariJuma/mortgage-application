package com.hfgroup.mortgage.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "applicant_id", nullable = false, updatable = false)
    private UUID applicantId;

    @Column(nullable = false, unique = true, length = 20)
    private String nationalId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // One application can have multiple documents
    @OneToMany(mappedBy = "applicationId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents;

    @PrePersist
    public void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}