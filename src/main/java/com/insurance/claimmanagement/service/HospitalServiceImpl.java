package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Hospital;
import com.insurance.claimmanagement.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Override
    public Hospital saveHospital(Hospital hospital) {
        try {
            return hospitalRepository.save(hospital);
        } catch (Exception e) {
            throw new RuntimeException("Error saving hospital: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<Hospital> getHospitalById(Long hospitalId) {
        try {
            return hospitalRepository.findById(hospitalId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching hospital: " + e.getMessage());
        }
    }
    
    @Override
    public List<Hospital> getAllHospitals() {
        try {
            return hospitalRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all hospitals: " + e.getMessage());
        }
    }
    
    @Override
    public Hospital updateHospital(Hospital hospital) {
        try {
            Optional<Hospital> existingHospital = hospitalRepository.findById(hospital.getHospitalId());
            if (existingHospital.isPresent()) {
                return hospitalRepository.save(hospital);
            } else {
                throw new RuntimeException("Hospital not found with ID: " + hospital.getHospitalId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating hospital: " + e.getMessage());
        }
    }
    
    @Override
    public Hospital patchHospital(Long hospitalId, Map<String, Object> updates) {
        try {
            Optional<Hospital> existingHospital = hospitalRepository.findById(hospitalId);
            if (existingHospital.isEmpty()) {
                throw new RuntimeException("Hospital not found with ID: " + hospitalId);
            }

            Hospital hospital = existingHospital.get();

            if (updates.containsKey("hospitalName") && updates.get("hospitalName") != null) {
                hospital.setHospitalName(updates.get("hospitalName").toString());
            }
            if (updates.containsKey("hospitalType") && updates.get("hospitalType") != null) {
                hospital.setHospitalType(updates.get("hospitalType").toString());
            }
            if (updates.containsKey("address") && updates.get("address") != null) {
                hospital.setAddress(updates.get("address").toString());
            }
            if (updates.containsKey("phoneNumber") && updates.get("phoneNumber") != null) {
                hospital.setPhoneNumber(updates.get("phoneNumber").toString());
            }

            return hospitalRepository.save(hospital);
        } catch (Exception e) {
            throw new RuntimeException("Error patching hospital: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteHospital(Long hospitalId) {
        try {
            if (hospitalRepository.existsById(hospitalId)) {
                hospitalRepository.deleteById(hospitalId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting hospital: " + e.getMessage());
        }
    }
}
