package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.UserPolicy;
import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.repository.UserPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserPolicyServiceImpl implements UserPolicyService {
    
    @Autowired
    private UserPolicyRepository userPolicyRepository;
    
    @Override
    @Transactional
    public UserPolicy saveUserPolicy(UserPolicy userPolicy) {
        try {
            if (userPolicy == null) {
                throw new IllegalArgumentException("User policy cannot be null");
            }
            if (userPolicy.getPurchasedDate() == null || userPolicy.getExpiryDate() == null) {
                throw new IllegalArgumentException("Purchased date and expiry date are required");
            }
            if (!userPolicy.getExpiryDate().isAfter(userPolicy.getPurchasedDate())) {
                throw new IllegalArgumentException("Expiry date must be after purchased date");
            }
            return userPolicyRepository.save(userPolicy);
        } catch (Exception e) {
            throw new RuntimeException("Error saving user policy: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<UserPolicy> getUserPolicyById(Long userPolicyId) {
        try {
            return userPolicyRepository.findById(userPolicyId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user policy: " + e.getMessage());
        }
    }
    
    @Override
    public List<UserPolicy> getAllUserPolicies() {
        try {
            return userPolicyRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all user policies: " + e.getMessage());
        }
    }
    
    @Override
    public List<UserPolicy> getPoliciesByUser(User user) {
        try {
            return userPolicyRepository.findByUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching policies by user: " + e.getMessage());
        }
    }
    
    @Override
    public UserPolicy updateUserPolicy(UserPolicy userPolicy) {
        try {
            Optional<UserPolicy> existingUserPolicy = userPolicyRepository.findById(userPolicy.getUserPolicyId());
            if (existingUserPolicy.isPresent()) {
                return userPolicyRepository.save(userPolicy);
            } else {
                throw new RuntimeException("User policy not found with ID: " + userPolicy.getUserPolicyId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating user policy: " + e.getMessage());
        }
    }
    
    @Override
    public UserPolicy patchUserPolicy(Long userPolicyId, Map<String, Object> updates) {
        try {
            Optional<UserPolicy> existingUserPolicy = userPolicyRepository.findById(userPolicyId);
            if (existingUserPolicy.isEmpty()) {
                throw new RuntimeException("User policy not found with ID: " + userPolicyId);
            }

            UserPolicy userPolicy = existingUserPolicy.get();

            if (updates.containsKey("purchasedDate") && updates.get("purchasedDate") != null) {
                userPolicy.setPurchasedDate(LocalDate.parse(updates.get("purchasedDate").toString()));
            }
            if (updates.containsKey("expiryDate") && updates.get("expiryDate") != null) {
                userPolicy.setExpiryDate(LocalDate.parse(updates.get("expiryDate").toString()));
            }
            if (updates.containsKey("policyActiveStatus") && updates.get("policyActiveStatus") != null) {
                userPolicy.setPolicyActiveStatus(updates.get("policyActiveStatus").toString());
            }
            if (userPolicy.getPurchasedDate() != null && userPolicy.getExpiryDate() != null && !userPolicy.getExpiryDate().isAfter(userPolicy.getPurchasedDate())) {
                throw new IllegalArgumentException("Expiry date must be after purchased date");
            }

            return userPolicyRepository.save(userPolicy);
        } catch (Exception e) {
            throw new RuntimeException("Error patching user policy: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteUserPolicy(Long userPolicyId) {
        try {
            if (userPolicyRepository.existsById(userPolicyId)) {
                userPolicyRepository.deleteById(userPolicyId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user policy: " + e.getMessage());
        }
    }
}
