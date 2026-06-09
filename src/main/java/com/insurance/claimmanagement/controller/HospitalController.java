package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.Hospital;
import com.insurance.claimmanagement.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class HospitalController {
    
    @Autowired
    private HospitalService hospitalService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHospitals() {
        try {
            List<Hospital> hospitals = hospitalService.getAllHospitals();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Hospitals fetched successfully");
            response.put("status", true);
            response.put("data", hospitals);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching hospitals: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{hospitalId}")
    public ResponseEntity<Map<String, Object>> getHospitalById(@PathVariable Long hospitalId) {
        try {
            Optional<Hospital> hospital = hospitalService.getHospitalById(hospitalId);
            Map<String, Object> response = new HashMap<>();
            
            if (hospital.isPresent()) {
                response.put("message", "Hospital fetched successfully");
                response.put("status", true);
                response.put("data", hospital.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Hospital not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching hospital: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createHospital(@RequestBody Hospital hospital) {
        try {
            Hospital savedHospital = hospitalService.saveHospital(hospital);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Hospital created successfully");
            response.put("status", true);
            response.put("data", savedHospital);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error creating hospital: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{hospitalId}")
    public ResponseEntity<Map<String, Object>> updateHospital(@PathVariable Long hospitalId, @RequestBody Hospital hospital) {
        try {
            Optional<Hospital> existingHospital = hospitalService.getHospitalById(hospitalId);
            if (existingHospital.isPresent()) {
                hospital.setHospitalId(hospitalId);
                Hospital updatedHospital = hospitalService.updateHospital(hospital);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Hospital updated successfully");
                response.put("status", true);
                response.put("data", updatedHospital);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Hospital not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error updating hospital: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PatchMapping("/{hospitalId}")
    public ResponseEntity<Map<String, Object>> patchHospital(@PathVariable Long hospitalId, @RequestBody Map<String, Object> updates) {
        try {
            Hospital patchedHospital = hospitalService.patchHospital(hospitalId, updates);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Hospital patched successfully");
            response.put("status", true);
            response.put("data", patchedHospital);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error patching hospital: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{hospitalId}")
    public ResponseEntity<Map<String, Object>> deleteHospital(@PathVariable Long hospitalId) {
        try {
            boolean deleted = hospitalService.deleteHospital(hospitalId);
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("message", "Hospital deleted successfully");
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Hospital not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error deleting hospital: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
