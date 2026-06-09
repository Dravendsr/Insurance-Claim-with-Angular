package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.InsurancePolicy;
import com.insurance.claimmanagement.repository.InsurancePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InsurancePolicyServiceImpl implements InsurancePolicyService {
    
    @Autowired
    private InsurancePolicyRepository policyRepository;
    
    @Override
    public InsurancePolicy savePolicy(InsurancePolicy policy) {
        try {
            if (policy == null) {
                throw new IllegalArgumentException("Policy cannot be null");
            }
            if (policy.getPolicyNumber() == null || policy.getPolicyNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Policy number is required");
            }
            if (policy.getPolicyName() == null || policy.getPolicyName().trim().isEmpty()) {
                throw new IllegalArgumentException("Policy name is required");
            }
            policyRepository.findByPolicyNumber(policy.getPolicyNumber().trim()).ifPresent(existing -> {
                if (policy.getPolicyId() == null || !existing.getPolicyId().equals(policy.getPolicyId())) {
                    throw new IllegalArgumentException("Policy number already exists");
                }
            });
            if (policy.getCoverageAmount() == null || policy.getCoverageAmount() < 0) {
                throw new IllegalArgumentException("Coverage amount must be a non-negative value");
            }
            if (policy.getPremiumAmount() == null || policy.getPremiumAmount() < 0) {
                throw new IllegalArgumentException("Premium amount must be a non-negative value");
            }
            if (policy.getStartDate() == null || policy.getEndDate() == null) {
                throw new IllegalArgumentException("Policy start date and end date are required");
            }
            if (!policy.getEndDate().isAfter(policy.getStartDate())) {
                throw new IllegalArgumentException("Policy end date must be after start date");
            }
            policy.setUpdatedAt(LocalDateTime.now());
            return policyRepository.save(policy);
        } catch (Exception e) {
            throw new RuntimeException("Error saving policy: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<InsurancePolicy> getPolicyById(Long policyId) {
        try {
            return policyRepository.findById(policyId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching policy: " + e.getMessage());
        }
    }
    
    @Override
    public List<InsurancePolicy> getAllPolicies() {
        try {
            return policyRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all policies: " + e.getMessage());
        }
    }
    
    @Override
    public InsurancePolicy updatePolicy(InsurancePolicy policy) {
        try {
            Optional<InsurancePolicy> existingPolicyOptional = policyRepository.findById(policy.getPolicyId());
            if (existingPolicyOptional.isPresent()) {
                InsurancePolicy existingPolicy = existingPolicyOptional.get();
                policyRepository.findByPolicyNumber(policy.getPolicyNumber().trim()).ifPresent(conflict -> {
                    if (!conflict.getPolicyId().equals(policy.getPolicyId())) {
                        throw new IllegalArgumentException("Policy number already exists");
                    }
                });
                existingPolicy.setPolicyNumber(policy.getPolicyNumber());
                existingPolicy.setPolicyName(policy.getPolicyName());
                existingPolicy.setPolicyType(policy.getPolicyType());
                existingPolicy.setCoverageAmount(policy.getCoverageAmount());
                existingPolicy.setPremiumAmount(policy.getPremiumAmount());
                existingPolicy.setBenefits(policy.getBenefits());
                existingPolicy.setPolicyStatus(policy.getPolicyStatus());
                existingPolicy.setStartDate(policy.getStartDate());
                existingPolicy.setEndDate(policy.getEndDate());
                existingPolicy.setUpdatedAt(LocalDateTime.now());
                return policyRepository.save(existingPolicy);
            } else {
                throw new RuntimeException("Policy not found with ID: " + policy.getPolicyId());
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating policy: " + e.getMessage());
        }
    }
    
    @Override
    public InsurancePolicy patchPolicy(Long policyId, Map<String, Object> updates) {
        try {
            Optional<InsurancePolicy> existingPolicy = policyRepository.findById(policyId);
            if (existingPolicy.isEmpty()) {
                throw new RuntimeException("Policy not found with ID: " + policyId);
            }

            InsurancePolicy policy = existingPolicy.get();

            if (updates.containsKey("policyNumber") && updates.get("policyNumber") != null) {
                policy.setPolicyNumber(updates.get("policyNumber").toString());
            }
            if (updates.containsKey("policyName") && updates.get("policyName") != null) {
                policy.setPolicyName(updates.get("policyName").toString());
            }
            if (updates.containsKey("policyType") && updates.get("policyType") != null) {
                policy.setPolicyType(updates.get("policyType").toString());
            }
            if (updates.containsKey("coverageAmount") && updates.get("coverageAmount") != null) {
                policy.setCoverageAmount(Double.valueOf(updates.get("coverageAmount").toString()));
            }
            if (updates.containsKey("premiumAmount") && updates.get("premiumAmount") != null) {
                policy.setPremiumAmount(Double.valueOf(updates.get("premiumAmount").toString()));
            }
            if (updates.containsKey("benefits")) {
                policy.setBenefits(updates.get("benefits") == null ? null : updates.get("benefits").toString());
            }
            if (updates.containsKey("policyStatus") && updates.get("policyStatus") != null) {
                policy.setPolicyStatus(updates.get("policyStatus").toString());
            }
            LocalDate effectiveStartDate = policy.getStartDate();
            LocalDate effectiveEndDate = policy.getEndDate();
            if (updates.containsKey("startDate") && updates.get("startDate") != null) {
                effectiveStartDate = LocalDate.parse(updates.get("startDate").toString());
                policy.setStartDate(effectiveStartDate);
            }
            if (updates.containsKey("endDate") && updates.get("endDate") != null) {
                effectiveEndDate = LocalDate.parse(updates.get("endDate").toString());
                policy.setEndDate(effectiveEndDate);
            }
            if (effectiveStartDate != null && effectiveEndDate != null && !effectiveEndDate.isAfter(effectiveStartDate)) {
                throw new IllegalArgumentException("Policy end date must be after start date");
            }
            if (policy.getCoverageAmount() != null && policy.getCoverageAmount() < 0) {
                throw new IllegalArgumentException("Coverage amount must be a non-negative value");
            }
            if (policy.getPremiumAmount() != null && policy.getPremiumAmount() < 0) {
                throw new IllegalArgumentException("Premium amount must be a non-negative value");
            }
            policy.setUpdatedAt(LocalDateTime.now());

            return policyRepository.save(policy);
        } catch (Exception e) {
            throw new RuntimeException("Error patching policy: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deletePolicy(Long policyId) {
        try {
            if (policyRepository.existsById(policyId)) {
                policyRepository.deleteById(policyId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting policy: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<InsurancePolicy> getPolicyByNumber(String policyNumber) {
        try {
            return policyRepository.findByPolicyNumber(policyNumber);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching policy by number: " + e.getMessage());
        }
    }
}
