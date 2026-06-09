package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.Payment;
import com.insurance.claimmanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payments fetched successfully");
            response.put("status", true);
            response.put("data", payments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching payments: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentById(@PathVariable Long paymentId) {
        try {
            Optional<Payment> payment = paymentService.getPaymentById(paymentId);
            Map<String, Object> response = new HashMap<>();
            
            if (payment.isPresent()) {
                response.put("message", "Payment fetched successfully");
                response.put("status", true);
                response.put("data", payment.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Payment not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching payment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getPaymentsByUser(@PathVariable Long userId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payments fetched successfully");
            response.put("status", true);
            response.put("data", payments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching payments: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/status/{paymentStatus}")
    public ResponseEntity<Map<String, Object>> getPaymentsByStatus(@PathVariable String paymentStatus) {
        try {
            List<Payment> payments = paymentService.getPaymentsByStatus(paymentStatus);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payments fetched successfully");
            response.put("status", true);
            response.put("data", payments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching payments: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Payment payment) {
        try {
            Payment savedPayment = paymentService.savePayment(payment);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment created successfully");
            response.put("status", true);
            response.put("data", savedPayment);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error creating payment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{paymentId}/process")
    public ResponseEntity<Map<String, Object>> processPayment(@PathVariable Long paymentId) {
        try {
            Payment processedPayment = paymentService.processPayment(paymentId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment processed successfully");
            response.put("status", true);
            response.put("data", processedPayment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error processing payment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> deletePayment(@PathVariable Long paymentId) {
        try {
            boolean deleted = paymentService.deletePayment(paymentId);
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("message", "Payment deleted successfully");
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Payment not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error deleting payment: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
