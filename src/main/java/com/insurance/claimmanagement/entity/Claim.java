package com.insurance.claimmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "claims")
public class Claim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;
    
    @Column(unique = true, nullable = false, length = 50)
    private String claimNumber;
    
    @Column(nullable = false)
    private Double claimAmount;
    
    @Column(columnDefinition = "DOUBLE DEFAULT 0")
    private Double approvedAmount;
    
    @Column(nullable = false, length = 20)
    private String claimStatus; // PENDING, APPROVED, REJECTED, SETTLED
    
    @Column(columnDefinition = "TEXT")
    private String approverComment;
    
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(length = 20)
    private String recommendationStatus;

    @Column(columnDefinition = "TEXT")
    private String recommendationReason;

    private Integer recommendationScore;
    
    @Column(nullable = false)
    private LocalDate claimDate;
    
    @Column(columnDefinition = "DATE DEFAULT NULL")
    private LocalDate approvedDate;
    
    @Column(columnDefinition = "DATE DEFAULT NULL")
    private LocalDate rejectedDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy insurancePolicy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_approver_id")
    private User assignedApprover;
    
    // One Claim has Many Documents
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("claim-document")
    private List<Document> documents = new ArrayList<>();
    
    // One Claim has One Payment
    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("claim-payment")
    private Payment payment;
    
    // Constructor
    public Claim() {
        this.claimStatus = "PENDING";
        this.approvedAmount = 0.0;
        this.claimDate = LocalDate.now();
    }
    
    public Claim(String claimNumber, Double claimAmount, String claimStatus,
                User user, Treatment treatment, InsurancePolicy insurancePolicy) {
        this.claimNumber = claimNumber;
        this.claimAmount = claimAmount;
        this.claimStatus = claimStatus;
        this.user = user;
        this.treatment = treatment;
        this.insurancePolicy = insurancePolicy;
        this.approvedAmount = 0.0;
        this.claimDate = LocalDate.now();
    }
    
    // Getters and Setters
    public Long getClaimId() {
        return claimId;
    }
    
    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }
    
    public String getClaimNumber() {
        return claimNumber;
    }
    
    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }
    
    public Double getClaimAmount() {
        return claimAmount;
    }
    
    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }
    
    public Double getApprovedAmount() {
        return approvedAmount;
    }
    
    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }
    
    public String getClaimStatus() {
        return claimStatus;
    }
    
    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }
    
    public String getApproverComment() {
        return approverComment;
    }
    
    public void setApproverComment(String approverComment) {
        this.approverComment = approverComment;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public String getRecommendationStatus() {
        return recommendationStatus;
    }
    
    public void setRecommendationStatus(String recommendationStatus) {
        this.recommendationStatus = recommendationStatus;
    }
    
    public String getRecommendationReason() {
        return recommendationReason;
    }
    
    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }
    
    public Integer getRecommendationScore() {
        return recommendationScore;
    }
    
    public void setRecommendationScore(Integer recommendationScore) {
        this.recommendationScore = recommendationScore;
    }
    
    public LocalDate getClaimDate() {
        return claimDate;
    }
    
    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }
    
    public LocalDate getApprovedDate() {
        return approvedDate;
    }
    
    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }
    
    public LocalDate getRejectedDate() {
        return rejectedDate;
    }
    
    public void setRejectedDate(LocalDate rejectedDate) {
        this.rejectedDate = rejectedDate;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Treatment getTreatment() {
        return treatment;
    }
    
    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }
    
    public InsurancePolicy getInsurancePolicy() {
        return insurancePolicy;
    }
    
    public void setInsurancePolicy(InsurancePolicy insurancePolicy) {
        this.insurancePolicy = insurancePolicy;
    }
    
    public Long getPolicyId() {
        return this.insurancePolicy != null ? this.insurancePolicy.getPolicyId() : null;
    }
    
    public User getAssignedApprover() {
        return assignedApprover;
    }
    
    public void setAssignedApprover(User assignedApprover) {
        this.assignedApprover = assignedApprover;
    }
    
    public List<Document> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    public Payment getPayment() {
        return payment;
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    @Override
    public String toString() {
        return "Claim{" +
                "claimId=" + claimId +
                ", claimNumber='" + claimNumber + '\'' +
                ", claimAmount=" + claimAmount +
                ", claimStatus='" + claimStatus + '\'' +
                ", claimDate=" + claimDate +
                '}';
    }
}
