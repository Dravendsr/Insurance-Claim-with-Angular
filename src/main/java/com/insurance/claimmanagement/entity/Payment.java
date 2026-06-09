package com.insurance.claimmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    
    @Column(nullable = false)
    private Double paymentAmount;
    
    @Column(nullable = false)
    private LocalDate paymentDate;
    
    @Column(nullable = false, length = 50)
    private String paymentMode; // Bank Transfer, Cheque, Online, etc.
    
    @Column(length = 50)
    private String transactionId;
    
    @Column(nullable = false, length = 20)
    private String paymentStatus; // PENDING, COMPLETED, FAILED
    
    @Column(nullable = false, length = 20)
    private String companyAccountNumber;
    
    @Column(nullable = false, length = 100)
    private String companyBankName;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    @JsonBackReference("claim-payment")
    private Claim claim;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructor
    public Payment() {
        this.paymentDate = LocalDate.now();
        this.paymentStatus = "PENDING";
    }
    
    public Payment(Double paymentAmount, LocalDate paymentDate, String paymentMode,
                  String transactionId, String paymentStatus, String companyAccountNumber,
                  String companyBankName, Claim claim, User user) {
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentMode = paymentMode;
        this.transactionId = transactionId;
        this.paymentStatus = paymentStatus;
        this.companyAccountNumber = companyAccountNumber;
        this.companyBankName = companyBankName;
        this.claim = claim;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public Double getPaymentAmount() {
        return paymentAmount;
    }
    
    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getPaymentMode() {
        return paymentMode;
    }
    
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getCompanyAccountNumber() {
        return companyAccountNumber;
    }
    
    public void setCompanyAccountNumber(String companyAccountNumber) {
        this.companyAccountNumber = companyAccountNumber;
    }
    
    public String getCompanyBankName() {
        return companyBankName;
    }
    
    public void setCompanyBankName(String companyBankName) {
        this.companyBankName = companyBankName;
    }
    
    public Claim getClaim() {
        return claim;
    }
    
    public void setClaim(Claim claim) {
        this.claim = claim;
    }
    
    public Long getClaimId() {
        return this.claim != null ? this.claim.getClaimId() : null;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", paymentAmount=" + paymentAmount +
                ", paymentDate=" + paymentDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
