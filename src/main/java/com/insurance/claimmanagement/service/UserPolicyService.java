package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.UserPolicy;
import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.entity.InsurancePolicy;
import java.util.List;
import java.util.Optional;

public interface UserPolicyService {
    UserPolicy saveUserPolicy(UserPolicy userPolicy);
    Optional<UserPolicy> getUserPolicyById(Long userPolicyId);
    List<UserPolicy> getAllUserPolicies();
    List<UserPolicy> getPoliciesByUser(User user);
    UserPolicy updateUserPolicy(UserPolicy userPolicy);
    UserPolicy patchUserPolicy(Long userPolicyId, java.util.Map<String, Object> updates);
    boolean deleteUserPolicy(Long userPolicyId);
}
