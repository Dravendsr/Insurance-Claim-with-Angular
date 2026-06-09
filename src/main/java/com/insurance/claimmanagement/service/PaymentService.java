package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Payment;
import com.insurance.claimmanagement.entity.Claim;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment savePayment(Payment payment);
    Optional<Payment> getPaymentById(Long paymentId);
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentByClaim(Claim claim);
    List<Payment> getPaymentsByUserId(Long userId);
    List<Payment> getPaymentsByStatus(String status);
    Payment updatePayment(Payment payment);
    boolean deletePayment(Long paymentId);
    Payment processPayment(Long paymentId);
}
