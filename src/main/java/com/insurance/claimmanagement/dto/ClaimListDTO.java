package com.insurance.claimmanagement.dto;

import java.time.LocalDate;

public class ClaimListDTO {
    private Long claimId;
    private String claimNumber;
    private Long policyId;
    private String claimStatus;
    private Double claimAmount;
    private LocalDate claimDate;

    public ClaimListDTO(Long claimId, String claimNumber, Long policyId, String claimStatus, Double claimAmount, LocalDate claimDate) {
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.policyId = policyId;
        this.claimStatus = claimStatus;
        this.claimAmount = claimAmount;
        this.claimDate = claimDate;
    }

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

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }
}
