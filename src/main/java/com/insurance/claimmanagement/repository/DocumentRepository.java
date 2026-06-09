package com.insurance.claimmanagement.repository;

import com.insurance.claimmanagement.entity.Document;
import com.insurance.claimmanagement.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByClaim(Claim claim);
    List<Document> findByClaimUserUserId(Long userId);
}
