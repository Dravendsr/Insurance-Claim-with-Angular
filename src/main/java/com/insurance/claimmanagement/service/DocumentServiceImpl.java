package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Document;
import com.insurance.claimmanagement.entity.Claim;
import com.insurance.claimmanagement.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Override
    public Document saveDocument(Document document) {
        try {
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Error saving document: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<Document> getDocumentById(Long documentId) {
        try {
            return documentRepository.findById(documentId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching document: " + e.getMessage());
        }
    }
    
    @Override
    public List<Document> getAllDocuments() {
        try {
            return documentRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all documents: " + e.getMessage());
        }
    }
    
    @Override
    public List<Document> getDocumentsByUserId(Long userId) {
        try {
            return documentRepository.findByClaimUserUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching documents by user ID: " + e.getMessage());
        }
    }
    
    @Override
    public List<Document> getDocumentsByClaim(Claim claim) {
        try {
            return documentRepository.findByClaim(claim);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching documents by claim: " + e.getMessage());
        }
    }
    
    @Override
    public Document updateDocument(Document document) {
        try {
            Optional<Document> existingDocument = documentRepository.findById(document.getDocumentId());
            if (existingDocument.isPresent()) {
                return documentRepository.save(document);
            } else {
                throw new RuntimeException("Document not found with ID: " + document.getDocumentId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating document: " + e.getMessage());
        }
    }
    
    @Override
    public Document patchDocument(Long documentId, Map<String, Object> updates) {
        try {
            Optional<Document> existingDocument = documentRepository.findById(documentId);
            if (existingDocument.isEmpty()) {
                throw new RuntimeException("Document not found with ID: " + documentId);
            }

            Document document = existingDocument.get();

            if (updates.containsKey("documentName") && updates.get("documentName") != null) {
                document.setDocumentName(updates.get("documentName").toString());
            }
            if (updates.containsKey("documentType") && updates.get("documentType") != null) {
                document.setDocumentType(updates.get("documentType").toString());
            }
            if (updates.containsKey("documentPath") && updates.get("documentPath") != null) {
                document.setDocumentPath(updates.get("documentPath").toString());
            }
            if (updates.containsKey("uploadedDate") && updates.get("uploadedDate") != null) {
                document.setUploadedDate(LocalDateTime.parse(updates.get("uploadedDate").toString()));
            }

            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Error patching document: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteDocument(Long documentId) {
        try {
            if (documentRepository.existsById(documentId)) {
                documentRepository.deleteById(documentId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting document: " + e.getMessage());
        }
    }
}
