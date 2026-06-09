package com.insurance.claimmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "documents")
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;
    
    @Column(nullable = false, length = 100)
    private String documentName;
    
    @Column(nullable = false, length = 50)
    private String documentType; // Medical Report, Receipt, Prescription, etc.
    
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String documentPath;
    
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime uploadedDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    @JsonBackReference("claim-document")
    private Claim claim;
    
    // Constructor
    public Document() {
        this.uploadedDate = LocalDateTime.now();
    }
    
    public Document(String documentName, String documentType, String documentPath, Claim claim) {
        this.documentName = documentName;
        this.documentType = documentType;
        this.documentPath = documentPath;
        this.claim = claim;
        this.uploadedDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
    
    public String getDocumentName() {
        return documentName;
    }
    
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    
    public String getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    public String getDocumentPath() {
        return documentPath;
    }
    
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
    
    public LocalDateTime getUploadedDate() {
        return uploadedDate;
    }
    
    public void setUploadedDate(LocalDateTime uploadedDate) {
        this.uploadedDate = uploadedDate;
    }
    
    public Claim getClaim() {
        return claim;
    }
    
    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    @JsonProperty("claimId")
    public Long getClaimId() {
        return this.claim != null ? this.claim.getClaimId() : null;
    }

    @JsonProperty("claimNumber")
    public String getClaimNumber() {
        return this.claim != null ? this.claim.getClaimNumber() : null;
    }
    
    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", documentName='" + documentName + '\'' +
                ", documentType='" + documentType + '\'' +
                ", uploadedDate=" + uploadedDate +
                '}';
    }
}
