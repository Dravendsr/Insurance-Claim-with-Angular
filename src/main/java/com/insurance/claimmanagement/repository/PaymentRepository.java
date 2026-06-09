package com.insurance.claimmanagement.repository;

import com.insurance.claimmanagement.entity.Payment;
import com.insurance.claimmanagement.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByClaim(Claim claim);
    List<Payment> findByPaymentStatus(String status);
    List<Payment> findByUserUserId(Long userId);

    @Query("SELECT COALESCE(SUM(p.paymentAmount), 0) FROM Payment p WHERE p.paymentStatus = 'COMPLETED'")
    Double sumCompletedPaymentAmount();
}
