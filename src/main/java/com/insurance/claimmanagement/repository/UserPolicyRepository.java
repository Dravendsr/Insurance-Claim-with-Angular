package com.insurance.claimmanagement.repository;

import com.insurance.claimmanagement.entity.UserPolicy;
import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.entity.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {
    List<UserPolicy> findByUser(User user);
    List<UserPolicy> findByInsurancePolicy(InsurancePolicy policy);
    Optional<UserPolicy> findByUserAndInsurancePolicy(User user, InsurancePolicy policy);
}
