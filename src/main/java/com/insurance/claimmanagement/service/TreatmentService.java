package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Treatment;
import com.insurance.claimmanagement.entity.User;
import java.util.List;
import java.util.Optional;

public interface TreatmentService {
    Treatment saveTreatment(Treatment treatment);
    Optional<Treatment> getTreatmentById(Long treatmentId);
    List<Treatment> getAllTreatments();
    List<Treatment> getTreatmentsByUser(User user);
    Treatment updateTreatment(Treatment treatment);
    Treatment patchTreatment(Long treatmentId, java.util.Map<String, Object> updates);
    boolean deleteTreatment(Long treatmentId);
}
