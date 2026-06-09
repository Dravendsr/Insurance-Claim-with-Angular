package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.Doctor;
import com.insurance.claimmanagement.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Doctors fetched successfully");
            response.put("status", true);
            response.put("data", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching doctors: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{doctorId}")
    public ResponseEntity<Map<String, Object>> getDoctorById(@PathVariable Long doctorId) {
        try {
            Optional<Doctor> doctor = doctorService.getDoctorById(doctorId);
            Map<String, Object> response = new HashMap<>();
            
            if (doctor.isPresent()) {
                response.put("message", "Doctor fetched successfully");
                response.put("status", true);
                response.put("data", doctor.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Doctor not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching doctor: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDoctor(@RequestBody Doctor doctor) {
        try {
            Doctor savedDoctor = doctorService.saveDoctor(doctor);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Doctor created successfully");
            response.put("status", true);
            response.put("data", savedDoctor);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error creating doctor: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{doctorId}")
    public ResponseEntity<Map<String, Object>> updateDoctor(@PathVariable Long doctorId, @RequestBody Doctor doctor) {
        try {
            Optional<Doctor> existingDoctor = doctorService.getDoctorById(doctorId);
            if (existingDoctor.isPresent()) {
                doctor.setDoctorId(doctorId);
                Doctor updatedDoctor = doctorService.updateDoctor(doctor);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Doctor updated successfully");
                response.put("status", true);
                response.put("data", updatedDoctor);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Doctor not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error updating doctor: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PatchMapping("/{doctorId}")
    public ResponseEntity<Map<String, Object>> patchDoctor(@PathVariable Long doctorId, @RequestBody Map<String, Object> updates) {
        try {
            Doctor patchedDoctor = doctorService.patchDoctor(doctorId, updates);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Doctor patched successfully");
            response.put("status", true);
            response.put("data", patchedDoctor);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error patching doctor: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable Long doctorId) {
        try {
            boolean deleted = doctorService.deleteDoctor(doctorId);
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("message", "Doctor deleted successfully");
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Doctor not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error deleting doctor: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
