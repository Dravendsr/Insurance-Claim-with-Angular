package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.Treatment;
import com.insurance.claimmanagement.service.TreatmentService;
import com.insurance.claimmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/treatments")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TreatmentController {
    
    @Autowired
    private TreatmentService treatmentService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTreatments() {
        try {
            List<Treatment> treatments = treatmentService.getAllTreatments();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Treatments fetched successfully");
            response.put("status", true);
            response.put("data", treatments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching treatments: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{treatmentId}")
    public ResponseEntity<Map<String, Object>> getTreatmentById(@PathVariable Long treatmentId) {
        try {
            Optional<Treatment> treatment = treatmentService.getTreatmentById(treatmentId);
            Map<String, Object> response = new HashMap<>();
            
            if (treatment.isPresent()) {
                response.put("message", "Treatment fetched successfully");
                response.put("status", true);
                response.put("data", treatment.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Treatment not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching treatment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTreatment(@RequestBody Treatment treatment) {
        try {
            Treatment savedTreatment = treatmentService.saveTreatment(treatment);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Treatment created successfully");
            response.put("status", true);
            response.put("data", savedTreatment);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error creating treatment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{treatmentId}")
    public ResponseEntity<Map<String, Object>> updateTreatment(@PathVariable Long treatmentId, @RequestBody Treatment treatment) {
        try {
            Optional<Treatment> existingTreatment = treatmentService.getTreatmentById(treatmentId);
            if (existingTreatment.isPresent()) {
                treatment.setTreatmentId(treatmentId);
                Treatment updatedTreatment = treatmentService.updateTreatment(treatment);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Treatment updated successfully");
                response.put("status", true);
                response.put("data", updatedTreatment);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Treatment not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error updating treatment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PatchMapping("/{treatmentId}")
    public ResponseEntity<Map<String, Object>> patchTreatment(@PathVariable Long treatmentId, @RequestBody Map<String, Object> updates) {
        try {
            Treatment patchedTreatment = treatmentService.patchTreatment(treatmentId, updates);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Treatment patched successfully");
            response.put("status", true);
            response.put("data", patchedTreatment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error patching treatment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{treatmentId}")
    public ResponseEntity<Map<String, Object>> deleteTreatment(@PathVariable Long treatmentId) {
        try {
            boolean deleted = treatmentService.deleteTreatment(treatmentId);
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("message", "Treatment deleted successfully");
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Treatment not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error deleting treatment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
