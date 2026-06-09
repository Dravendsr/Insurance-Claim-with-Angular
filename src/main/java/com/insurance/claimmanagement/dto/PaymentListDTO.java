package com.insurance.claimmanagement.dto;

import java.time.LocalDate;

public class PaymentListDTO {
    private Long paymentId;
    private Long claimId;
    private Long userId;
    private Double paymentAmount;
    private LocalDate paymentDate;
    private String paymentStatus;
    private String paymentMode;

    public PaymentListDTO(Long paymentId, Long claimId, Long userId, Double paymentAmount, LocalDate paymentDate, String paymentStatus, String paymentMode) {
        this.paymentId = paymentId;
        this.claimId = claimId;
        this.userId = userId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
        this.paymentMode = paymentMode;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
