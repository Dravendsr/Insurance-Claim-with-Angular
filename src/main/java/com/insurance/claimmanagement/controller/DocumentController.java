package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.Document;
import com.insurance.claimmanagement.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDocuments() {
        try {
            List<Document> documents = documentService.getAllDocuments();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Documents fetched successfully");
            response.put("status", true);
            response.put("data", documents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching documents: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getDocumentsByUser(@PathVariable Long userId) {
        try {
            List<Document> documents = documentService.getDocumentsByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Documents fetched successfully");
            response.put("status", true);
            response.put("data", documents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching documents: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> getDocumentById(@PathVariable Long documentId) {
        try {
            Optional<Document> document = documentService.getDocumentById(documentId);
            Map<String, Object> response = new HashMap<>();
            
            if (document.isPresent()) {
                response.put("message", "Document fetched successfully");
                response.put("status", true);
                response.put("data", document.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Document not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching document: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadDocument(@RequestBody Document document) {
        try {
            Document savedDocument = documentService.saveDocument(document);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document uploaded successfully");
            response.put("status", true);
            response.put("data", savedDocument);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error uploading document: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> updateDocument(@PathVariable Long documentId, @RequestBody Document document) {
        try {
            Optional<Document> existingDocument = documentService.getDocumentById(documentId);
            if (existingDocument.isPresent()) {
                document.setDocumentId(documentId);
                Document updatedDocument = documentService.updateDocument(document);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Document updated successfully");
                response.put("status", true);
                response.put("data", updatedDocument);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Document not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error updating document: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PatchMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> patchDocument(@PathVariable Long documentId, @RequestBody Map<String, Object> updates) {
        try {
            Document patchedDocument = documentService.patchDocument(documentId, updates);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document patched successfully");
            response.put("status", true);
            response.put("data", patchedDocument);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error patching document: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable Long documentId) {
        try {
            boolean deleted = documentService.deleteDocument(documentId);
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("message", "Document deleted successfully");
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Document not found");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error deleting document: " + e.getMessage());
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
