package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Claim;
import com.insurance.claimmanagement.entity.Treatment;
import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.entity.Doctor;
import com.insurance.claimmanagement.entity.Hospital;
import com.insurance.claimmanagement.entity.Payment;
import com.insurance.claimmanagement.repository.ClaimRepository;
import com.insurance.claimmanagement.repository.TreatmentRepository;
import com.insurance.claimmanagement.repository.DoctorRepository;
import com.insurance.claimmanagement.repository.HospitalRepository;
import com.insurance.claimmanagement.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.insurance.claimmanagement.service.InsurancePolicyService;
import com.insurance.claimmanagement.service.UserService;

@Service
public class ClaimServiceImpl implements ClaimService {
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Autowired
    private InsurancePolicyService insurancePolicyService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TreatmentRepository treatmentRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Override
    public Claim saveClaim(Claim claim) {
        try {
            if (claim == null) {
                throw new IllegalArgumentException("Claim cannot be null");
            }
            if (claim.getClaimNumber() == null || claim.getClaimNumber().isEmpty()) {
                throw new IllegalArgumentException("Claim number is required");
            }
            if (claimRepository.findByClaimNumber(claim.getClaimNumber()).isPresent() && claim.getClaimId() == null) {
                throw new IllegalArgumentException("Claim number already exists");
            }
            if (claim.getClaimAmount() == null || claim.getClaimAmount() < 0) {
                throw new IllegalArgumentException("Claim amount must be a non-negative value");
            }
            if (claim.getClaimDate() != null && claim.getClaimDate().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Claim date cannot be in the future");
            }
            if (claim.getClaimStatus() == null || claim.getClaimStatus().isEmpty()) {
                claim.setClaimStatus("PENDING");
            }
            if (claim.getClaimDate() == null) {
                claim.setClaimDate(LocalDate.now());
            }
            if (claim.getInsurancePolicy() != null && claim.getInsurancePolicy().getPolicyId() != null) {
                insurancePolicyService.getPolicyById(claim.getInsurancePolicy().getPolicyId())
                        .ifPresent(claim::setInsurancePolicy);
            }
            if (claim.getUser() != null && claim.getUser().getUserId() != null) {
                userService.getUserById(claim.getUser().getUserId()).ifPresent(claim::setUser);
            }
            if (claim.getTreatment() != null) {
                Treatment treatment = claim.getTreatment();
                if (treatment.getUser() == null && claim.getUser() != null) {
                    treatment.setUser(claim.getUser());
                }
                
                // Handle Hospital creation/linking first (since Doctor requires Hospital)
                if (treatment.getHospitalName() != null && !treatment.getHospitalName().isEmpty()) {
                    Hospital hospital = null;
                    if (treatment.getHospital() != null && treatment.getHospital().getHospitalId() != null) {
                        hospital = treatment.getHospital();
                    } else {
                        // Create new hospital if not exists
                        hospital = new Hospital();
                        hospital.setHospitalName(treatment.getHospitalName());
                        hospital.setAddress(treatment.getHospitalAddress() != null ? treatment.getHospitalAddress() : "");
                        hospital.setPhoneNumber(treatment.getHospitalPhone() != null ? treatment.getHospitalPhone() : "");
                        hospital.setHospitalType("Private"); // Default type
                        hospital = hospitalRepository.save(hospital);
                    }
                    treatment.setHospital(hospital);
                }
                
                // Handle Doctor creation/linking (after hospital is created)
                if (treatment.getDoctorName() != null && !treatment.getDoctorName().isEmpty()) {
                    Doctor doctor = null;
                    if (treatment.getDoctor() != null && treatment.getDoctor().getDoctorId() != null) {
                        doctor = treatment.getDoctor();
                    } else {
                        // Create new doctor if not exists
                        doctor = new Doctor();
                        doctor.setDoctorName(treatment.getDoctorName());
                        doctor.setSpecialization(treatment.getDoctorSpecialization() != null ? treatment.getDoctorSpecialization() : "");
                        doctor.setQualification(treatment.getDoctorQualification() != null ? treatment.getDoctorQualification() : "MBBS");
                        doctor.setExperienceYears(treatment.getDoctorExperienceYears() != null ? treatment.getDoctorExperienceYears() : 0);
                        doctor.setHospital(treatment.getHospital()); // Link to hospital
                        doctor = doctorRepository.save(doctor);
                    }
                    treatment.setDoctor(doctor);
                }
                
                if (treatment.getTreatmentId() == null) {
                    treatment = treatmentRepository.save(treatment);
                    claim.setTreatment(treatment);
                }
            }
            calculateRecommendation(claim);
            return claimRepository.save(claim);
        } catch (Exception e) {
            throw new RuntimeException("Error saving claim: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Claim> getClaimById(Long claimId) {
        try {
            return claimRepository.findById(claimId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching claim: " + e.getMessage());
        }
    }
    
    @Override
    public List<Claim> getAllClaims() {
        try {
            return claimRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all claims: " + e.getMessage());
        }
    }
    
    @Override
    public List<Claim> getClaimsByUser(User user) {
        try {
            return claimRepository.findByUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching claims by user: " + e.getMessage());
        }
    }
    
    @Override
    public List<Claim> getClaimsByStatus(String status) {
        try {
            return claimRepository.findByClaimStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching claims by status: " + e.getMessage());
        }
    }
    
    @Override
    public List<Claim> getClaimsByApprover(User approver) {
        try {
            return claimRepository.findByAssignedApprover(approver);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching claims by approver: " + e.getMessage());
        }
    }
    
    @Override
    public Claim updateClaim(Claim claim) {
        try {
            Optional<Claim> existingClaim = claimRepository.findById(claim.getClaimId());
            if (existingClaim.isPresent()) {
                if (claim.getInsurancePolicy() != null && claim.getInsurancePolicy().getPolicyId() != null) {
                    insurancePolicyService.getPolicyById(claim.getInsurancePolicy().getPolicyId())
                            .ifPresent(claim::setInsurancePolicy);
                }
                if (claim.getUser() != null && claim.getUser().getUserId() != null) {
                    userService.getUserById(claim.getUser().getUserId()).ifPresent(claim::setUser);
                }
                if (claim.getTreatment() != null) {
                    Treatment treatment = claim.getTreatment();
                    if (treatment.getUser() == null && claim.getUser() != null) {
                        treatment.setUser(claim.getUser());
                    }
                    if (treatment.getTreatmentId() == null) {
                        treatment = treatmentRepository.save(treatment);
                        claim.setTreatment(treatment);
                    }
                }
                calculateRecommendation(claim);
                return claimRepository.save(claim);
            } else {
                throw new RuntimeException("Claim not found with ID: " + claim.getClaimId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating claim: " + e.getMessage());
        }
    }
    
    @Override
    public Claim patchClaim(Long claimId, Map<String, Object> updates) {
        try {
            Optional<Claim> existingClaim = claimRepository.findById(claimId);
            if (existingClaim.isEmpty()) {
                throw new RuntimeException("Claim not found with ID: " + claimId);
            }

            Claim claim = existingClaim.get();

            if (updates.containsKey("claimAmount") && updates.get("claimAmount") != null) {
                Double claimAmount = Double.valueOf(updates.get("claimAmount").toString());
                if (claimAmount < 0) {
                    throw new IllegalArgumentException("Claim amount must be non-negative");
                }
                claim.setClaimAmount(claimAmount);
            }
            if (updates.containsKey("approvedAmount") && updates.get("approvedAmount") != null) {
                Double approvedAmount = Double.valueOf(updates.get("approvedAmount").toString());
                if (approvedAmount < 0) {
                    throw new IllegalArgumentException("Approved amount must be non-negative");
                }
                claim.setApprovedAmount(approvedAmount);
            }
            if (updates.containsKey("claimStatus") && updates.get("claimStatus") != null) {
                String newStatus = updates.get("claimStatus").toString().trim().toUpperCase();
                String currentStatus = claim.getClaimStatus() != null ? claim.getClaimStatus().trim().toUpperCase() : "";
                if ("APPROVED".equals(currentStatus) && "APPROVED".equals(newStatus)) {
                    throw new IllegalStateException("Claim is already approved");
                }
                if ("SETTLED".equals(currentStatus) && !"SETTLED".equals(newStatus)) {
                    throw new IllegalStateException("Cannot modify a settled claim");
                }
                if ("REJECTED".equals(currentStatus) && "APPROVED".equals(newStatus)) {
                    throw new IllegalStateException("Cannot approve a rejected claim");
                }
                claim.setClaimStatus(newStatus);
            }
            if (updates.containsKey("approverComment")) {
                claim.setApproverComment(updates.get("approverComment") == null ? null : updates.get("approverComment").toString());
            }
            if (updates.containsKey("rejectionReason")) {
                claim.setRejectionReason(updates.get("rejectionReason") == null ? null : updates.get("rejectionReason").toString());
            }
            if (updates.containsKey("assignedApprover")) {
                Object assignedApproverValue = updates.get("assignedApprover");
                if (assignedApproverValue != null) {
                    Long approverId;
                    if (assignedApproverValue instanceof Number) {
                        approverId = ((Number) assignedApproverValue).longValue();
                    } else {
                        approverId = Long.valueOf(assignedApproverValue.toString());
                    }
                    if (claim.getAssignedApprover() != null && !claim.getAssignedApprover().getUserId().equals(approverId)) {
                        throw new IllegalStateException("Claim is already assigned to another approver");
                    }
                    userService.getUserById(approverId).ifPresentOrElse(
                            claim::setAssignedApprover,
                            () -> {
                                throw new RuntimeException("Approver not found with ID: " + approverId);
                            }
                    );
                    if (claim.getClaimStatus() != null && claim.getClaimStatus().trim().equalsIgnoreCase("SUBMITTED")) {
                        claim.setClaimStatus("PENDING");
                    }
                } else {
                    claim.setAssignedApprover(null);
                }
            }
            if (updates.containsKey("approvedDate") && updates.get("approvedDate") != null) {
                LocalDate approvedDate = LocalDate.parse(updates.get("approvedDate").toString());
                if (claim.getClaimDate() != null && approvedDate.isBefore(claim.getClaimDate())) {
                    throw new IllegalArgumentException("Approved date cannot be before claim date");
                }
                claim.setApprovedDate(approvedDate);
            }

            calculateRecommendation(claim);
            return claimRepository.save(claim);
        } catch (Exception e) {
            throw new RuntimeException("Error patching claim: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteClaim(Long claimId) {
        try {
            if (claimRepository.existsById(claimId)) {
                claimRepository.deleteById(claimId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting claim: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Claim approveClaim(Long claimId, Double approvedAmount, String approverComment) {
        try {
            Optional<Claim> claimOptional = claimRepository.findById(claimId);
            if (claimOptional.isPresent()) {
                Claim claim = claimOptional.get();
                String currentStatus = claim.getClaimStatus() != null ? claim.getClaimStatus().trim().toUpperCase() : "";
                if ("APPROVED".equals(currentStatus)) {
                    throw new RuntimeException("Claim is already approved");
                }
                if ("SETTLED".equals(currentStatus)) {
                    throw new RuntimeException("Cannot approve a settled claim");
                }
                if ("REJECTED".equals(currentStatus)) {
                    throw new RuntimeException("Cannot approve a rejected claim");
                }
                if (approvedAmount == null || approvedAmount < 0) {
                    throw new IllegalArgumentException("Approved amount must be a non-negative value");
                }
                if (claim.getClaimAmount() == null) {
                    throw new RuntimeException("Original claim amount is missing");
                }
                claim.setClaimStatus("APPROVED");
                claim.setApprovedAmount(approvedAmount);
                claim.setApprovedDate(LocalDate.now());
                
                // If approved amount < claim amount, comment is mandatory
                if (approvedAmount < claim.getClaimAmount()) {
                    if (approverComment == null || approverComment.isEmpty()) {
                        throw new RuntimeException("Approver comment is mandatory when approved amount is less than claim amount");
                    }
                    claim.setApproverComment(approverComment);
                }

                if (approverComment != null && !approverComment.isEmpty()) {
                    claim.setApproverComment(approverComment);
                }

                Payment existingPayment = paymentRepository.findByClaim(claim).orElse(null);
                if (existingPayment == null) {
                    Payment payment = new Payment();
                    payment.setPaymentAmount(approvedAmount);
                    payment.setPaymentMode("Automatic");
                    payment.setTransactionId("AUTO-" + UUID.randomUUID());
                    payment.setPaymentStatus("PENDING");
                    payment.setCompanyAccountNumber("INSURANCE-HQ-001");
                    payment.setCompanyBankName("Health Insurance Co.");
                    payment.setClaim(claim);
                    payment.setUser(claim.getUser());
                    paymentRepository.save(payment);
                }
                
                return claimRepository.save(claim);
            } else {
                throw new RuntimeException("Claim not found with ID: " + claimId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error approving claim: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Claim rejectClaim(Long claimId, String rejectionReason) {
        try {
            Optional<Claim> claimOptional = claimRepository.findById(claimId);
            if (claimOptional.isPresent()) {
                if (rejectionReason == null || rejectionReason.isEmpty()) {
                    throw new RuntimeException("Rejection reason is mandatory");
                }
                Claim claim = claimOptional.get();
                String currentStatus = claim.getClaimStatus() != null ? claim.getClaimStatus().trim().toUpperCase() : "";
                if ("SETTLED".equals(currentStatus)) {
                    throw new RuntimeException("Cannot reject a settled claim");
                }
                if ("REJECTED".equals(currentStatus)) {
                    throw new RuntimeException("Claim is already rejected");
                }
                if ("APPROVED".equals(currentStatus)) {
                    throw new RuntimeException("Cannot reject an approved claim");
                }
                claim.setClaimStatus("REJECTED");
                claim.setRejectionReason(rejectionReason);
                claim.setRejectedDate(LocalDate.now());
                return claimRepository.save(claim);
            } else {
                throw new RuntimeException("Claim not found with ID: " + claimId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error rejecting claim: " + e.getMessage());
        }
    }

    private void calculateRecommendation(Claim claim) {
        if (claim == null) {
            return;
        }

        int score = 100;
        List<String> reasons = new ArrayList<>();
        LocalDate now = LocalDate.now();
        Double claimAmount = claim.getClaimAmount();
        Double treatmentAmount = claim.getTreatment() != null ? claim.getTreatment().getTreatmentAmount() : null;
        Double coverageAmount = claim.getInsurancePolicy() != null ? claim.getInsurancePolicy().getCoverageAmount() : null;

        // Policy validation
        boolean policyValid = false;
        if (claim.getInsurancePolicy() != null
                && claim.getInsurancePolicy().getPolicyStatus() != null
                && claim.getInsurancePolicy().getPolicyStatus().equalsIgnoreCase("ACTIVE")
                && claim.getInsurancePolicy().getEndDate() != null
                && !now.isAfter(claim.getInsurancePolicy().getEndDate())) {
            policyValid = true;
        }
        if (policyValid) {
            reasons.add("Policy Active");
        } else {
            reasons.add("Policy Expired");
            score -= 50;
        }

        // Required document validation
        if (claim.getDocuments() == null || claim.getDocuments().isEmpty()) {
            reasons.add("No Supporting Documents Uploaded");
            score -= 30;
        } else {
            reasons.add("Supporting Documents Uploaded");
        }

        // Claim amount vs treatment cost validation
        if (claimAmount != null && treatmentAmount != null) {
            if (claimAmount > treatmentAmount) {
                reasons.add("Claim Amount Exceeds Treatment Cost");
                score -= 30;
            } else {
                reasons.add("Claim Amount Matches Treatment Cost");
            }
        }

        // Claim coverage validation
        if (coverageAmount != null && claimAmount != null) {
            if (claimAmount > coverageAmount) {
                reasons.add("Coverage Limit Exceeded");
                score -= 40;
            } else if (claimAmount > coverageAmount * 0.8) {
                reasons.add("High Coverage Utilization");
                score -= 15;
            } else {
                reasons.add("Within Coverage Limit");
            }
        }

        // High-risk user validation
        List<Claim> userClaims = claim.getUser() != null ? claimRepository.findByUser(claim.getUser()) : new ArrayList<>();
        long rejectedClaimsCount = userClaims.stream()
                .filter(existing -> existing.getClaimId() != null && !existing.getClaimId().equals(claim.getClaimId()))
                .filter(existing -> existing.getClaimStatus() != null && existing.getClaimStatus().equalsIgnoreCase("REJECTED"))
                .count();
        if (rejectedClaimsCount >= 3) {
            reasons.add("High Risk Claim History");
            score -= 25;
        } else {
            reasons.add("Normal Claim History");
        }

        // Repeated diagnosis validation within 180 days
        LocalDate thresholdDate = now.minusDays(180);
        long repeatedDiagnosisCount = userClaims.stream()
                .filter(existing -> existing.getClaimId() != null && !existing.getClaimId().equals(claim.getClaimId()))
                .filter(existing -> existing.getClaimDate() != null && !existing.getClaimDate().isBefore(thresholdDate))
                .filter(existing -> existing.getTreatment() != null && existing.getTreatment().getDiagnosis() != null)
                .filter(existing -> claim.getTreatment() != null && claim.getTreatment().getDiagnosis() != null
                        && existing.getTreatment().getDiagnosis().equalsIgnoreCase(claim.getTreatment().getDiagnosis()))
                .count();
        if (repeatedDiagnosisCount > 0) {
            reasons.add("Repeated Diagnosis Claim Found");
            score -= 20;
        } else {
            reasons.add("No Repeated Diagnosis Pattern");
        }

        score = Math.max(score, 0);
        claim.setRecommendationScore(score);

        if (score >= 80) {
            claim.setRecommendationStatus("APPROVE");
        } else if (score >= 50) {
            claim.setRecommendationStatus("REVIEW");
        } else {
            claim.setRecommendationStatus("REJECT");
        }

        claim.setRecommendationReason(String.join(", ", reasons));
    }
}

