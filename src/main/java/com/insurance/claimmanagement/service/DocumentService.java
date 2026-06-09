package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.Document;
import com.insurance.claimmanagement.entity.Claim;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    Document saveDocument(Document document);
    Optional<Document> getDocumentById(Long documentId);
    List<Document> getAllDocuments();
    List<Document> getDocumentsByUserId(Long userId);
    List<Document> getDocumentsByClaim(Claim claim);
    Document updateDocument(Document document);
    Document patchDocument(Long documentId, java.util.Map<String, Object> updates);
    boolean deleteDocument(Long documentId);
}
