package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.repository.ClaimRepository;
import com.insurance.claimmanagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminAnalyticsController {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getAnalyticsOverview() {
        try {
            long totalClaims = claimRepository.count();
            long totalApprovedClaims = claimRepository.countByClaimStatus("APPROVED");
            long totalRejectedClaims = claimRepository.countByClaimStatus("REJECTED");
            long totalPendingClaims = claimRepository.countByClaimStatus("PENDING");
            long totalSettledClaims = claimRepository.countByClaimStatus("SETTLED");

            Double totalApprovedAmount = claimRepository.sumApprovedClaimAmount();
            Double totalPaidAmount = paymentRepository.sumCompletedPaymentAmount();
            if (totalApprovedAmount == null) {
                totalApprovedAmount = 0.0;
            }
            if (totalPaidAmount == null) {
                totalPaidAmount = 0.0;
            }

            double approvalPercentage = totalClaims > 0 ? (totalApprovedClaims * 100.0) / totalClaims : 0.0;
            double rejectionPercentage = totalClaims > 0 ? (totalRejectedClaims * 100.0) / totalClaims : 0.0;

            Map<String, Object> data = new HashMap<>();
            data.put("totalClaims", totalClaims);
            data.put("totalPendingClaims", totalPendingClaims);
            data.put("totalApprovedClaims", totalApprovedClaims);
            data.put("totalRejectedClaims", totalRejectedClaims);
            data.put("totalSettledClaims", totalSettledClaims);
            data.put("approvalPercentage", approvalPercentage);
            data.put("rejectionPercentage", rejectionPercentage);
            data.put("totalApprovedAmount", totalApprovedAmount);
            data.put("totalPaidAmount", totalPaidAmount);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin analytics overview fetched successfully");
            response.put("status", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching analytics overview: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/top-hospitals")
    public ResponseEntity<Map<String, Object>> getTopHospitals() {
        try {
            List<Object[]> results = claimRepository.findTopHospitalsAnalytics();
            List<Map<String, Object>> hospitals = new ArrayList<>();
            for (Object[] result : results) {
                Map<String, Object> item = new HashMap<>();
                item.put("hospitalName", result[0]);
                item.put("claimCount", result[1]);
                item.put("approvedClaimCount", result[2]);
                item.put("totalClaimAmount", result[3]);
                hospitals.add(item);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Top hospitals fetched successfully");
            response.put("status", true);
            response.put("data", hospitals);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching top hospitals: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/top-doctors")
    public ResponseEntity<Map<String, Object>> getTopDoctors() {
        try {
            List<Object[]> results = claimRepository.findTopDoctorsAnalytics();
            List<Map<String, Object>> doctors = new ArrayList<>();
            for (Object[] result : results) {
                Map<String, Object> item = new HashMap<>();
                item.put("doctorName", result[0]);
                item.put("specialization", result[1]);
                item.put("claimCount", result[2]);
                item.put("approvedClaimCount", result[3]);
                doctors.add(item);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Top doctors fetched successfully");
            response.put("status", true);
            response.put("data", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching top doctors: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/high-risk-users")
    public ResponseEntity<Map<String, Object>> getHighRiskUsers() {
        try {
            List<Object[]> results = claimRepository.findHighRiskUsersAnalytics();
            List<Map<String, Object>> users = new ArrayList<>();
            for (Object[] result : results) {
                long totalClaims = ((Number) result[2]).longValue();
                long rejectedClaims = ((Number) result[3]).longValue();
                double rejectionPercentage = totalClaims > 0 ? (rejectedClaims * 100.0) / totalClaims : 0.0;

                Map<String, Object> item = new HashMap<>();
                item.put("userId", result[0]);
                item.put("userName", result[1]);
                item.put("totalClaims", totalClaims);
                item.put("rejectedClaims", rejectedClaims);
                item.put("rejectionPercentage", rejectionPercentage);
                users.add(item);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("message", "High-risk users fetched successfully");
            response.put("status", true);
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching high-risk users: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/treatment-statistics")
    public ResponseEntity<Map<String, Object>> getTreatmentStatistics() {
        try {
            List<Object[]> results = claimRepository.findTreatmentStatisticsAnalytics();
            List<Map<String, Object>> stats = new ArrayList<>();
            for (Object[] result : results) {
                Map<String, Object> item = new HashMap<>();
                item.put("diagnosis", result[0]);
                item.put("claimCount", result[1]);
                item.put("totalClaimAmount", result[2]);
                item.put("averageClaimAmount", result[3]);
                stats.add(item);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Treatment statistics fetched successfully");
            response.put("status", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching treatment statistics: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendationSummary() {
        try {
            long totalRecommendedApprovals = claimRepository.countByRecommendationStatus("APPROVE");
            long totalRecommendedReviews = claimRepository.countByRecommendationStatus("REVIEW");
            long totalRecommendedRejections = claimRepository.countByRecommendationStatus("REJECT");

            Map<String, Object> data = new HashMap<>();
            data.put("totalRecommendedApprovals", totalRecommendedApprovals);
            data.put("totalRecommendedReviews", totalRecommendedReviews);
            data.put("totalRecommendedRejections", totalRecommendedRejections);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Recommendation summary fetched successfully");
            response.put("status", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching recommendation summary: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
