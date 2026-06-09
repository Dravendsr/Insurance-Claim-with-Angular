package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.Claim;
import com.insurance.claimmanagement.service.ClaimService;
import com.insurance.claimmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ClaimController {
    
    @Autowired
    private ClaimService claimService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClaims() {
        try {
            List<Claim> claims = claimService.getAllClaims();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Claims fetched successfully");
            response.put("status", true);
            response.put("data", claims);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching claims: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{claimId}")
    public ResponseEntity<Map<String, Object>> getClaimById(@PathVariable Long claimId) {
        try {
            Optional<Claim> claim = claimService.getClaimById(claimId);
            Map<String, Object> response = new HashMap<>();
            
            if (claim.isPresent()) {
                response.put("message", "Claim fetched successfully");
                response.put("status", true);
                response.put("data", claim.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Claim not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching claim: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getClaimsByUser(@PathVariable Long userId) {
        try {
            Optional<com.insurance.claimmanagement.entity.User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                List<Claim> claims = claimService.getClaimsByUser(user.get());
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Claims fetched successfully");
                response.put("status", true);
                response.put("data", claims);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching claims: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getClaimsByStatus(@PathVariable String status) {
        try {
            List<Claim> claims = claimService.getClaimsByStatus(status);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Claims fetched successfully");
            response.put("status", true);
            response.put("data", claims);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching claims: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/approver/{approverId}")
    public ResponseEntity<Map<String, Object>> getClaimsByApprover(@PathVariable Long approverId) {
        try {
            Optional<com.insurance.claimmanagement.entity.User> user = userService.getUserById(approverId);
            if (user.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Approver not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            List<Claim> claims = claimService.getClaimsByApprover(user.get());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Claims fetched successfully");
            response.put("status", true);
            response.put("data", claims);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching claims: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createClaim(@RequestBody Claim claim) {
        try {
            Claim savedClaim = claimService.saveClaim(claim);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Claim created successfully");
            response.put("status", true);
            response.put("data", savedClaim);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error creating claim: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PatchMapping("/{claimId}")
    public ResponseEntity<Map<String, Object>> patchClaim(@PathVariable Long claimId, @RequestBody Map<String, Object> updates) {
        try {
            Claim patchedClaim = claimService.patchClaim(claimId, updates);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Claim updated successfully");
            response.put("status", true);
            response.put("data", patchedClaim);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error updating claim: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{claimId}/approve")
    public ResponseEntity<Map<String, Object>> approveClaim(@PathVariable Long claimId, @RequestBody Map<String, Object> approvalData) {
        try {
            Double approvedAmount = ((Number) approvalData.get("approvedAmount")).doubleValue();
            String approverComment = (String) approvalData.get("approverComment");
            
            Claim approvedClaim = claimService.approveClaim(claimId, approvedAmount, approverComment);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Claim approved successfully");
            response.put("status", true);
            response.put("data", approvedClaim);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error approving claim: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{claimId}/reject")
    public ResponseEntity<Map<String, Object>> rejectClaim(@PathVariable Long claimId, @RequestBody Map<String, String> rejectData) {
        try {
            String rejectionReason = rejectData.get("rejectionReason");
            
            Claim rejectedClaim = claimService.rejectClaim(claimId, rejectionReason);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Claim rejected successfully");
            response.put("status", true);
            response.put("data", rejectedClaim);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error rejecting claim: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{claimId}")
    public ResponseEntity<Map<String, Object>> deleteClaim(@PathVariable Long claimId) {
        try {
            boolean deleted = claimService.deleteClaim(claimId);
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("message", "Claim deleted successfully");
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Claim not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error deleting claim: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
