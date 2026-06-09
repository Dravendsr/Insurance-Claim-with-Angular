package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Treatment;
import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.repository.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TreatmentServiceImpl implements TreatmentService {
    
    @Autowired
    private TreatmentRepository treatmentRepository;
    
    @Override
    public Treatment saveTreatment(Treatment treatment) {
        try {
            return treatmentRepository.save(treatment);
        } catch (Exception e) {
            throw new RuntimeException("Error saving treatment: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<Treatment> getTreatmentById(Long treatmentId) {
        try {
            return treatmentRepository.findById(treatmentId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching treatment: " + e.getMessage());
        }
    }
    
    @Override
    public List<Treatment> getAllTreatments() {
        try {
            return treatmentRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all treatments: " + e.getMessage());
        }
    }
    
    @Override
    public List<Treatment> getTreatmentsByUser(User user) {
        try {
            return treatmentRepository.findByUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching treatments by user: " + e.getMessage());
        }
    }
    
    @Override
    public Treatment updateTreatment(Treatment treatment) {
        try {
            Optional<Treatment> existingTreatment = treatmentRepository.findById(treatment.getTreatmentId());
            if (existingTreatment.isPresent()) {
                return treatmentRepository.save(treatment);
            } else {
                throw new RuntimeException("Treatment not found with ID: " + treatment.getTreatmentId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating treatment: " + e.getMessage());
        }
    }
    
    @Override
    public Treatment patchTreatment(Long treatmentId, Map<String, Object> updates) {
        try {
            Optional<Treatment> existingTreatment = treatmentRepository.findById(treatmentId);
            if (existingTreatment.isEmpty()) {
                throw new RuntimeException("Treatment not found with ID: " + treatmentId);
            }

            Treatment treatment = existingTreatment.get();

            if (updates.containsKey("diagnosis") && updates.get("diagnosis") != null) {
                treatment.setDiagnosis(updates.get("diagnosis").toString());
            }
            if (updates.containsKey("treatmentDescription") && updates.get("treatmentDescription") != null) {
                treatment.setTreatmentDescription(updates.get("treatmentDescription").toString());
            }
            if (updates.containsKey("treatmentAmount") && updates.get("treatmentAmount") != null) {
                treatment.setTreatmentAmount(Double.valueOf(updates.get("treatmentAmount").toString()));
            }
            if (updates.containsKey("treatmentDate") && updates.get("treatmentDate") != null) {
                treatment.setTreatmentDate(LocalDate.parse(updates.get("treatmentDate").toString()));
            }

            return treatmentRepository.save(treatment);
        } catch (Exception e) {
            throw new RuntimeException("Error patching treatment: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteTreatment(Long treatmentId) {
        try {
            if (treatmentRepository.existsById(treatmentId)) {
                treatmentRepository.deleteById(treatmentId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting treatment: " + e.getMessage());
        }
    }
}
