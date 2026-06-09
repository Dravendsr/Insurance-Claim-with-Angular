package com.insurance.claimmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "user_policies")
public class UserPolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPolicyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-userPolicy")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonBackReference("policy-userPolicy")
    private InsurancePolicy insurancePolicy;
    
    @Column(nullable = false)
    private LocalDate purchasedDate;
    
    @Column(nullable = false)
    private LocalDate expiryDate;
    
    @Column(nullable = false, length = 20)
    private String policyActiveStatus; // ACTIVE, EXPIRED, SUSPENDED
    
    // Constructor
    public UserPolicy() {
    }
    
    public UserPolicy(User user, InsurancePolicy insurancePolicy, LocalDate purchasedDate,
                     LocalDate expiryDate, String policyActiveStatus) {
        this.user = user;
        this.insurancePolicy = insurancePolicy;
        this.purchasedDate = purchasedDate;
        this.expiryDate = expiryDate;
        this.policyActiveStatus = policyActiveStatus;
    }
    
    // Getters and Setters
    public Long getUserPolicyId() {
        return userPolicyId;
    }
    
    public void setUserPolicyId(Long userPolicyId) {
        this.userPolicyId = userPolicyId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public InsurancePolicy getInsurancePolicy() {
        return insurancePolicy;
    }
    
    public void setInsurancePolicy(InsurancePolicy insurancePolicy) {
        this.insurancePolicy = insurancePolicy;
    }
    
    public LocalDate getPurchasedDate() {
        return purchasedDate;
    }
    
    public void setPurchasedDate(LocalDate purchasedDate) {
        this.purchasedDate = purchasedDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getPolicyActiveStatus() {
        return policyActiveStatus;
    }
    
    public void setPolicyActiveStatus(String policyActiveStatus) {
        this.policyActiveStatus = policyActiveStatus;
    }
    
    @Override
    public String toString() {
        return "UserPolicy{" +
                "userPolicyId=" + userPolicyId +
                ", purchasedDate=" + purchasedDate +
                ", expiryDate=" + expiryDate +
                ", policyActiveStatus='" + policyActiveStatus + '\'' +
                '}';
    }
}
