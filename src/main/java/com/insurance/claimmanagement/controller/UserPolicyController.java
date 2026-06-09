package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.UserPolicy;
import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.entity.InsurancePolicy;
import com.insurance.claimmanagement.service.UserPolicyService;
import com.insurance.claimmanagement.service.UserService;
import com.insurance.claimmanagement.service.InsurancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-policies")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserPolicyController {
    
    @Autowired
    private UserPolicyService userPolicyService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private InsurancePolicyService policyService;
    
    // Get all user policies
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUserPolicies() {
        try {
            List<UserPolicy> userPolicies = userPolicyService.getAllUserPolicies();
            List<Map<String, Object>> responseData = userPolicies.stream()
                    .map(this::toUserPolicyResponse)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User policies fetched successfully");
            response.put("status", true);
            response.put("data", responseData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching user policies: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Get policies by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getPoliciesByUser(@PathVariable Long userId) {
        try {
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                List<UserPolicy> userPolicies = userPolicyService.getPoliciesByUser(user.get());
                List<Map<String, Object>> responseData = userPolicies.stream()
                        .map(this::toUserPolicyResponse)
                        .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Policies fetched successfully");
                response.put("status", true);
                response.put("data", responseData);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching policies: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Get user policy by ID
    @GetMapping("/{userPolicyId}")
    public ResponseEntity<Map<String, Object>> getUserPolicyById(@PathVariable Long userPolicyId) {
        try {
            Optional<UserPolicy> userPolicy = userPolicyService.getUserPolicyById(userPolicyId);
            Map<String, Object> response = new HashMap<>();
            
            if (userPolicy.isPresent()) {
                response.put("message", "User policy fetched successfully");
                response.put("status", true);
                response.put("data", toUserPolicyResponse(userPolicy.get()));
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "User policy not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching user policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private Map<String, Object> toUserPolicyResponse(UserPolicy userPolicy) {
        Map<String, Object> policyMap = new HashMap<>();
        policyMap.put("userPolicyId", userPolicy.getUserPolicyId());
        policyMap.put("purchasedDate", userPolicy.getPurchasedDate());
        policyMap.put("expiryDate", userPolicy.getExpiryDate());
        policyMap.put("policyActiveStatus", userPolicy.getPolicyActiveStatus());

        if (userPolicy.getInsurancePolicy() != null) {
            Map<String, Object> insurancePolicyMap = new HashMap<>();
            insurancePolicyMap.put("policyId", userPolicy.getInsurancePolicy().getPolicyId());
            insurancePolicyMap.put("policyNumber", userPolicy.getInsurancePolicy().getPolicyNumber());
            insurancePolicyMap.put("policyName", userPolicy.getInsurancePolicy().getPolicyName());
            insurancePolicyMap.put("policyType", userPolicy.getInsurancePolicy().getPolicyType());
            insurancePolicyMap.put("coverageAmount", userPolicy.getInsurancePolicy().getCoverageAmount());
            insurancePolicyMap.put("premiumAmount", userPolicy.getInsurancePolicy().getPremiumAmount());
            insurancePolicyMap.put("benefits", userPolicy.getInsurancePolicy().getBenefits());
            insurancePolicyMap.put("policyStatus", userPolicy.getInsurancePolicy().getPolicyStatus());
            insurancePolicyMap.put("startDate", userPolicy.getInsurancePolicy().getStartDate());
            insurancePolicyMap.put("endDate", userPolicy.getInsurancePolicy().getEndDate());
            policyMap.put("insurancePolicy", insurancePolicyMap);
        }

        return policyMap;
    }
    
    // Assign policy to user (Admin)
    @PostMapping
    public ResponseEntity<Map<String, Object>> assignPolicyToUser(@RequestBody UserPolicy userPolicy) {
        try {
            UserPolicy savedUserPolicy = userPolicyService.saveUserPolicy(userPolicy);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Policy assigned to user successfully");
            response.put("status", true);
            response.put("data", savedUserPolicy);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error assigning policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Update user policy
    @PutMapping("/{userPolicyId}")
    public ResponseEntity<Map<String, Object>> updateUserPolicy(@PathVariable Long userPolicyId, @RequestBody UserPolicy userPolicy) {
        try {
            Optional<UserPolicy> existingUserPolicy = userPolicyService.getUserPolicyById(userPolicyId);
            if (existingUserPolicy.isPresent()) {
                userPolicy.setUserPolicyId(userPolicyId);
                UserPolicy updatedUserPolicy = userPolicyService.updateUserPolicy(userPolicy);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User policy updated successfully");
                response.put("status", true);
                response.put("data", updatedUserPolicy);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User policy not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error updating user policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PatchMapping("/{userPolicyId}")
    public ResponseEntity<Map<String, Object>> patchUserPolicy(@PathVariable Long userPolicyId, @RequestBody Map<String, Object> updates) {
        try {
            UserPolicy patchedUserPolicy = userPolicyService.patchUserPolicy(userPolicyId, updates);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User policy patched successfully");
            response.put("status", true);
            response.put("data", patchedUserPolicy);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error patching user policy: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
