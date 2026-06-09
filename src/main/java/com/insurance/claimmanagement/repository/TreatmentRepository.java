package com.insurance.claimmanagement.repository;

import com.insurance.claimmanagement.entity.Treatment;
import com.insurance.claimmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    List<Treatment> findByUser(User user);
}
