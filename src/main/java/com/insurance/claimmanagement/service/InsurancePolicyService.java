package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.InsurancePolicy;
import java.util.List;
import java.util.Optional;

public interface InsurancePolicyService {
    InsurancePolicy savePolicy(InsurancePolicy policy);
    Optional<InsurancePolicy> getPolicyById(Long policyId);
    List<InsurancePolicy> getAllPolicies();
    InsurancePolicy updatePolicy(InsurancePolicy policy);
    InsurancePolicy patchPolicy(Long policyId, java.util.Map<String, Object> updates);
    boolean deletePolicy(Long policyId);
    Optional<InsurancePolicy> getPolicyByNumber(String policyNumber);
}
