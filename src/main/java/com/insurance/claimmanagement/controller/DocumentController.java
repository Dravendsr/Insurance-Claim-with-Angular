package com.insurance.claimmanagement.controller;

import com.insurance.claimmanagement.entity.Document;
import com.insurance.claimmanagement.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    // Serve document content for viewing/downloading in the browser.
    @GetMapping("/file/{documentId}")
    public ResponseEntity<?> serveDocument(@PathVariable Long documentId) {
        try {
            Optional<Document> documentOpt = documentService.getDocumentById(documentId);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
            }
            Document document = documentOpt.get();
            String path = document.getDocumentPath();
            if (path == null || path.isBlank()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No document content available");
            }

            // If stored as data URL (data:<mime>;base64,<data>) decode and return bytes
            if (path.startsWith("data:")) {
                int comma = path.indexOf(',');
                String meta = path.substring(5, comma);
                String base64 = path.substring(comma + 1);
                String mime = meta.split(";")[0];
                byte[] bytes = java.util.Base64.getDecoder().decode(base64);
                InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(bytes));
                return ResponseEntity.ok()
                        .contentLength(bytes.length)
                        .contentType(MediaType.parseMediaType(mime))
                        .body(resource);
            }

            // If path looks like a server-relative path, try to load from classpath:/static or filesystem
            if (path.startsWith("/")) {
                // Try classpath static folder first
                Path fileOnClasspath = Paths.get("src/main/resources/static" + path);
                if (Files.exists(fileOnClasspath)) {
                    Resource fileResource = new org.springframework.core.io.PathResource(fileOnClasspath.toAbsolutePath());
                    String mime = Files.probeContentType(fileOnClasspath);
                    MediaType mediaType = mime != null ? MediaType.parseMediaType(mime) : MediaType.APPLICATION_OCTET_STREAM;
                    return ResponseEntity.ok().contentType(mediaType).body(fileResource);
                }

                // Try as absolute/relative filesystem path (strip leading slash)
                Path fsPath = Paths.get(path.substring(1));
                if (Files.exists(fsPath)) {
                    Resource fileResource = new org.springframework.core.io.PathResource(fsPath.toAbsolutePath());
                    String mime = Files.probeContentType(fsPath);
                    MediaType mediaType = mime != null ? MediaType.parseMediaType(mime) : MediaType.APPLICATION_OCTET_STREAM;
                    return ResponseEntity.ok().contentType(mediaType).body(fileResource);
                }

                // Not found on server
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document file not found on server: " + path);
            }

            // If it's an HTTP URL, redirect the client to it
            if (path.startsWith("http://") || path.startsWith("https://")) {
                return ResponseEntity.status(HttpStatus.FOUND).header("Location", path).build();
            }

            // Fallback: return not supported
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Unsupported document path format");

        } catch (IOException ioe) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading document: " + ioe.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error serving document: " + e.getMessage());
        }
    }
}
