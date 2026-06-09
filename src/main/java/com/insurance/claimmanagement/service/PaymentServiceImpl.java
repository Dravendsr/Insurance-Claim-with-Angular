package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Payment;
import com.insurance.claimmanagement.entity.Claim;
import com.insurance.claimmanagement.repository.PaymentRepository;
import com.insurance.claimmanagement.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Override
    public Payment savePayment(Payment payment) {
        try {
            if (payment == null) {
                throw new IllegalArgumentException("Payment cannot be null");
            }
            if (payment.getPaymentAmount() == null || payment.getPaymentAmount() < 0) {
                throw new IllegalArgumentException("Payment amount must be a non-negative value");
            }
            if (payment.getPaymentDate() != null && payment.getPaymentDate().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Payment date cannot be in the future");
            }
            if (payment.getClaim() == null) {
                throw new IllegalArgumentException("Payment must be associated with a claim");
            }
            return paymentRepository.save(payment);
        } catch (Exception e) {
            throw new RuntimeException("Error saving payment: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<Payment> getPaymentById(Long paymentId) {
        try {
            return paymentRepository.findById(paymentId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching payment: " + e.getMessage());
        }
    }
    
    @Override
    public List<Payment> getAllPayments() {
        try {
            ensurePaymentsForApprovedClaims();
            return paymentRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all payments: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<Payment> getPaymentByClaim(Claim claim) {
        try {
            return paymentRepository.findByClaim(claim);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching payment by claim: " + e.getMessage());
        }
    }
    
    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        try {
            ensurePendingPaymentsForApprovedClaims(userId);
            return paymentRepository.findByUserUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching payments by user ID: " + e.getMessage());
        }
    }

    @Override
    public List<Payment> getPaymentsByStatus(String status) {
        try {
            return paymentRepository.findByPaymentStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching payments by status: " + e.getMessage());
        }
    }
    
    @Override
    public Payment updatePayment(Payment payment) {
        try {
            Optional<Payment> existingPayment = paymentRepository.findById(payment.getPaymentId());
            if (existingPayment.isPresent()) {
                return paymentRepository.save(payment);
            } else {
                throw new RuntimeException("Payment not found with ID: " + payment.getPaymentId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating payment: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deletePayment(Long paymentId) {
        try {
            if (paymentRepository.existsById(paymentId)) {
                paymentRepository.deleteById(paymentId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting payment: " + e.getMessage());
        }
    }
    
    private void ensurePaymentsForApprovedClaims() {
        List<Claim> approvedClaims = claimRepository.findByClaimStatus("APPROVED");
        for (Claim claim : approvedClaims) {
            if (paymentRepository.findByClaim(claim).isEmpty()) {
                createPendingPaymentForClaim(claim);
            }
        }
    }

    private void ensurePendingPaymentsForApprovedClaims(Long userId) {
        List<Claim> approvedClaims = claimRepository.findByUserUserIdAndClaimStatus(userId, "APPROVED");
        for (Claim claim : approvedClaims) {
            if (paymentRepository.findByClaim(claim).isEmpty()) {
                createPendingPaymentForClaim(claim);
            }
        }
    }

    private void createPendingPaymentForClaim(Claim claim) {
        if (claim == null || claim.getUser() == null) {
            return;
        }
        Payment payment = new Payment();
        payment.setPaymentAmount(claim.getApprovedAmount() != null ? claim.getApprovedAmount() : claim.getClaimAmount());
        payment.setPaymentMode("Automatic");
        payment.setTransactionId("AUTO-" + UUID.randomUUID());
        payment.setPaymentStatus("PENDING");
        payment.setCompanyAccountNumber("INSURANCE-HQ-001");
        payment.setCompanyBankName("Health Insurance Co.");
        payment.setClaim(claim);
        payment.setUser(claim.getUser());
        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment processPayment(Long paymentId) {
        try {
            Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
            if (paymentOptional.isPresent()) {
                Payment payment = paymentOptional.get();
                if (payment.getPaymentStatus() != null && payment.getPaymentStatus().trim().equalsIgnoreCase("COMPLETED")) {
                    throw new RuntimeException("Payment has already been processed");
                }
                Claim claim = payment.getClaim();
                if (claim == null) {
                    throw new RuntimeException("Associated claim is missing for payment processing");
                }
                String claimStatus = claim.getClaimStatus() != null ? claim.getClaimStatus().trim().toUpperCase() : "";
                if (!"APPROVED".equals(claimStatus)) {
                    throw new RuntimeException("Payment can only be processed for APPROVED claims");
                }
                if ("SETTLED".equals(claimStatus)) {
                    throw new RuntimeException("Cannot process payment for a settled claim");
                }
                payment.setPaymentStatus("COMPLETED");
                payment.setPaymentDate(LocalDate.now());
                
                claim.setClaimStatus("SETTLED");
                claimRepository.save(claim);
                
                return paymentRepository.save(payment);
            } else {
                throw new RuntimeException("Payment not found with ID: " + paymentId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing payment: " + e.getMessage());
        }
    }
}
