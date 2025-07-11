package com.example.fraud.repository;

import com.example.fraud.entity.FraudRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FraudRecordRepository extends JpaRepository<FraudRecord, UUID> {
    
    /**
     * Find fraud records by user ID
     */
    List<FraudRecord> findByUserId(String userId);
    
    /**
     * Find fraud records by transaction ID
     */
    Optional<FraudRecord> findByTransactionId(String transactionId);
    
    /**
     * Find fraud records by fraud type
     */
    List<FraudRecord> findByFraudType(String fraudType);
    
    /**
     * Find fraud records by risk level
     */
    List<FraudRecord> findByRiskLevel(String riskLevel);
    
    /**
     * Find fraud records created within a date range
     */
    List<FraudRecord> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find fraud records detected within a date range
     */
    List<FraudRecord> findByDetectedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find fraud records by verification status
     */
    List<FraudRecord> findByIsVerified(Boolean isVerified);
    
    /**
     * Find fraud records by user ID and risk level
     */
    List<FraudRecord> findByUserIdAndRiskLevel(String userId, String riskLevel);
    
    /**
     * Find fraud records with amount greater than specified value
     */
    List<FraudRecord> findByAmountGreaterThan(Double amount);
    
    /**
     * Count fraud records by user ID
     */
    long countByUserId(String userId);
    
    /**
     * Count fraud records by risk level
     */
    long countByRiskLevel(String riskLevel);
    
    /**
     * Find recent fraud records (last 30 days)
     */
    @Query("SELECT f FROM FraudRecord f WHERE f.createdAt >= :thirtyDaysAgo ORDER BY f.createdAt DESC")
    List<FraudRecord> findRecentFraudRecords(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    /**
     * Find high-risk unverified fraud records
     */
    @Query("SELECT f FROM FraudRecord f WHERE f.riskLevel = 'HIGH' AND f.isVerified = false ORDER BY f.createdAt DESC")
    List<FraudRecord> findHighRiskUnverifiedRecords();
    
    /**
     * Find top fraud types by count
     */
    @Query("SELECT f.fraudType, COUNT(f) as count FROM FraudRecord f GROUP BY f.fraudType ORDER BY count DESC")
    List<Object[]> findTopFraudTypes();
}
