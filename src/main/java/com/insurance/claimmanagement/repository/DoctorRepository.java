package com.insurance.claimmanagement.repository;

import com.insurance.claimmanagement.entity.Doctor;
import com.insurance.claimmanagement.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByHospital(Hospital hospital);
}
