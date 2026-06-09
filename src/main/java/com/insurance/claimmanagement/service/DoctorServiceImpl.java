package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Doctor;
import com.insurance.claimmanagement.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Override
    public Doctor saveDoctor(Doctor doctor) {
        try {
            return doctorRepository.save(doctor);
        } catch (Exception e) {
            throw new RuntimeException("Error saving doctor: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<Doctor> getDoctorById(Long doctorId) {
        try {
            return doctorRepository.findById(doctorId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching doctor: " + e.getMessage());
        }
    }
    
    @Override
    public List<Doctor> getAllDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all doctors: " + e.getMessage());
        }
    }
    
    @Override
    public Doctor updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctor.getDoctorId());
            if (existingDoctor.isPresent()) {
                return doctorRepository.save(doctor);
            } else {
                throw new RuntimeException("Doctor not found with ID: " + doctor.getDoctorId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating doctor: " + e.getMessage());
        }
    }
    
    @Override
    public Doctor patchDoctor(Long doctorId, Map<String, Object> updates) {
        try {
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctorId);
            if (existingDoctor.isEmpty()) {
                throw new RuntimeException("Doctor not found with ID: " + doctorId);
            }

            Doctor doctor = existingDoctor.get();

            if (updates.containsKey("doctorName") && updates.get("doctorName") != null) {
                doctor.setDoctorName(updates.get("doctorName").toString());
            }
            if (updates.containsKey("specialization") && updates.get("specialization") != null) {
                doctor.setSpecialization(updates.get("specialization").toString());
            }
            if (updates.containsKey("qualification") && updates.get("qualification") != null) {
                doctor.setQualification(updates.get("qualification").toString());
            }
            if (updates.containsKey("experienceYears") && updates.get("experienceYears") != null) {
                doctor.setExperienceYears(Integer.valueOf(updates.get("experienceYears").toString()));
            }

            return doctorRepository.save(doctor);
        } catch (Exception e) {
            throw new RuntimeException("Error patching doctor: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteDoctor(Long doctorId) {
        try {
            if (doctorRepository.existsById(doctorId)) {
                doctorRepository.deleteById(doctorId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting doctor: " + e.getMessage());
        }
    }
}
