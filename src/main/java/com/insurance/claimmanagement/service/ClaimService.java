package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Claim;
import com.insurance.claimmanagement.entity.User;
import java.util.List;
import java.util.Optional;

public interface ClaimService {
    Claim saveClaim(Claim claim);
    Optional<Claim> getClaimById(Long claimId);
    List<Claim> getAllClaims();
    List<Claim> getClaimsByUser(User user);
    List<Claim> getClaimsByStatus(String status);
    List<Claim> getClaimsByApprover(User approver);
    Claim updateClaim(Claim claim);
    Claim patchClaim(Long claimId, java.util.Map<String, Object> updates);
    boolean deleteClaim(Long claimId);
    Claim approveClaim(Long claimId, Double approvedAmount, String approverComment);
    Claim rejectClaim(Long claimId, String rejectionReason);
}
