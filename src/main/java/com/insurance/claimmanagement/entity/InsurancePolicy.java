package com.insurance.claimmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "insurance_policies")
public class InsurancePolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;
    
    @Column(unique = true, nullable = false, length = 50)
    private String policyNumber;
    
    @Column(nullable = false, length = 100)
    private String policyName;
    
    @Column(nullable = false, length = 50)
    private String policyType; // Individual, Family, Group, etc.
    
    @Column(nullable = false)
    private Double coverageAmount;
    
    @Column(nullable = false)
    private Double premiumAmount;
    
    @Column(columnDefinition = "TEXT")
    private String benefits;
    
    @Column(nullable = false, length = 20)
    private String policyStatus; // ACTIVE, INACTIVE
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    // One Policy has Many UserPolicies
    @OneToMany(mappedBy = "insurancePolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserPolicy> userPolicies = new ArrayList<>();
    
    // One Policy has Many Claims
    @OneToMany(mappedBy = "insurancePolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Claim> claims = new ArrayList<>();
    
    // Constructor
    public InsurancePolicy() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public InsurancePolicy(String policyNumber, String policyName, String policyType, Double coverageAmount,
                          Double premiumAmount, String benefits, String policyStatus,
                          LocalDate startDate, LocalDate endDate) {
        this.policyNumber = policyNumber;
        this.policyName = policyName;
        this.policyType = policyType;
        this.coverageAmount = coverageAmount;
        this.premiumAmount = premiumAmount;
        this.benefits = benefits;
        this.policyStatus = policyStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }
    
    public String getPolicyNumber() {
        return policyNumber;
    }
    
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    
    public String getPolicyName() {
        return policyName;
    }
    
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
    
    public String getPolicyType() {
        return policyType;
    }
    
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }
    
    public Double getCoverageAmount() {
        return coverageAmount;
    }
    
    public void setCoverageAmount(Double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }
    
    public Double getPremiumAmount() {
        return premiumAmount;
    }
    
    public void setPremiumAmount(Double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }
    
    public String getBenefits() {
        return benefits;
    }
    
    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }
    
    public String getPolicyStatus() {
        return policyStatus;
    }
    
    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<UserPolicy> getUserPolicies() {
        return userPolicies;
    }
    
    public void setUserPolicies(List<UserPolicy> userPolicies) {
        this.userPolicies = userPolicies;
    }
    
    public List<Claim> getClaims() {
        return claims;
    }
    
    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }
    
    @Override
    public String toString() {
        return "InsurancePolicy{" +
                "policyId=" + policyId +
                ", policyNumber='" + policyNumber + '\'' +
                ", policyName='" + policyName + '\'' +
                ", coverageAmount=" + coverageAmount +
                ", policyStatus='" + policyStatus + '\'' +
                '}';
    }
}
