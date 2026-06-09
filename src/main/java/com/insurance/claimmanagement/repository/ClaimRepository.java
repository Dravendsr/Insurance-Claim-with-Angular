package com.insurance.claimmanagement.repository;

import com.insurance.claimmanagement.entity.Claim;
import com.insurance.claimmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByUser(User user);
    List<Claim> findByUserUserIdAndClaimStatus(Long userId, String status);
    List<Claim> findByClaimStatus(String status);
    List<Claim> findByAssignedApprover(User approver);
    long countByClaimStatus(String status);
    long countByRecommendationStatus(String status);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(c.approvedAmount), 0) FROM Claim c WHERE c.claimStatus = 'APPROVED'")
    Double sumApprovedClaimAmount();

    @org.springframework.data.jpa.repository.Query("SELECT c.treatment.hospital.hospitalName, COUNT(c), " +
            "SUM(CASE WHEN c.claimStatus = 'APPROVED' THEN 1 ELSE 0 END), COALESCE(SUM(c.claimAmount), 0) " +
            "FROM Claim c GROUP BY c.treatment.hospital.hospitalId, c.treatment.hospital.hospitalName ORDER BY COUNT(c) DESC")
    List<Object[]> findTopHospitalsAnalytics();

    @org.springframework.data.jpa.repository.Query("SELECT c.treatment.doctor.doctorName, c.treatment.doctor.specialization, COUNT(c), " +
            "SUM(CASE WHEN c.claimStatus = 'APPROVED' THEN 1 ELSE 0 END) " +
            "FROM Claim c GROUP BY c.treatment.doctor.doctorId, c.treatment.doctor.doctorName, c.treatment.doctor.specialization ORDER BY COUNT(c) DESC")
    List<Object[]> findTopDoctorsAnalytics();

    @org.springframework.data.jpa.repository.Query("SELECT c.user.userId, c.user.fullName, COUNT(c), " +
            "SUM(CASE WHEN c.claimStatus = 'REJECTED' THEN 1 ELSE 0 END) " +
            "FROM Claim c GROUP BY c.user.userId, c.user.fullName " +
            "HAVING SUM(CASE WHEN c.claimStatus = 'REJECTED' THEN 1 ELSE 0 END) >= 3 " +
            "ORDER BY SUM(CASE WHEN c.claimStatus = 'REJECTED' THEN 1 ELSE 0 END) DESC")
    List<Object[]> findHighRiskUsersAnalytics();

    @org.springframework.data.jpa.repository.Query("SELECT c.treatment.diagnosis, COUNT(c), COALESCE(SUM(c.claimAmount), 0), COALESCE(AVG(c.claimAmount), 0) " +
            "FROM Claim c GROUP BY c.treatment.diagnosis ORDER BY COUNT(c) DESC")
    List<Object[]> findTreatmentStatisticsAnalytics();
}
