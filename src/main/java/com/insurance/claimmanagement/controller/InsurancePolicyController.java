package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.InsurancePolicy;
import com.insurance.claimmanagement.service.InsurancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class InsurancePolicyController {
    
    @Autowired
    private InsurancePolicyService policyService;
    
    // Get all policies
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPolicies() {
        try {
            List<InsurancePolicy> policies = policyService.getAllPolicies();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Policies fetched successfully");
            response.put("status", true);
            response.put("data", policies);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching policies: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Get policy by ID
    @GetMapping("/{policyId}")
    public ResponseEntity<Map<String, Object>> getPolicyById(@PathVariable Long policyId) {
        try {
            Optional<InsurancePolicy> policy = policyService.getPolicyById(policyId);
            Map<String, Object> response = new HashMap<>();
            
            if (policy.isPresent()) {
                response.put("message", "Policy fetched successfully");
                response.put("status", true);
                response.put("data", policy.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Policy not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Create new policy (Admin only)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPolicy(@RequestBody InsurancePolicy policy) {
        try {
            InsurancePolicy savedPolicy = policyService.savePolicy(policy);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Policy created successfully");
            response.put("status", true);
            response.put("data", savedPolicy);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error creating policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Update policy (Admin only)
    @PutMapping("/{policyId}")
    public ResponseEntity<Map<String, Object>> updatePolicy(@PathVariable Long policyId, @RequestBody InsurancePolicy policy) {
        try {
            Optional<InsurancePolicy> existingPolicy = policyService.getPolicyById(policyId);
            if (existingPolicy.isPresent()) {
                policy.setPolicyId(policyId);
                InsurancePolicy updatedPolicy = policyService.updatePolicy(policy);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Policy updated successfully");
                response.put("status", true);
                response.put("data", updatedPolicy);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Policy not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error updating policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PatchMapping("/{policyId}")
    public ResponseEntity<Map<String, Object>> patchPolicy(@PathVariable Long policyId, @RequestBody Map<String, Object> updates) {
        try {
            InsurancePolicy patchedPolicy = policyService.patchPolicy(policyId, updates);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Policy patched successfully");
            response.put("status", true);
            response.put("data", patchedPolicy);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error patching policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Delete policy (Admin only)
    @DeleteMapping("/{policyId}")
    public ResponseEntity<Map<String, Object>> deletePolicy(@PathVariable Long policyId) {
        try {
            boolean deleted = policyService.deletePolicy(policyId);
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("message", "Policy deleted successfully");
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Policy not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error deleting policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
