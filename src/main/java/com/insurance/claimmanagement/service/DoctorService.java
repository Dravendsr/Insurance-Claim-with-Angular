package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    Doctor saveDoctor(Doctor doctor);
    Optional<Doctor> getDoctorById(Long doctorId);
    List<Doctor> getAllDoctors();
    Doctor updateDoctor(Doctor doctor);
    Doctor patchDoctor(Long doctorId, java.util.Map<String, Object> updates);
    boolean deleteDoctor(Long doctorId);
}
