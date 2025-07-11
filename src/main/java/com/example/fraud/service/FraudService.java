package com.example.fraud.service;

import com.example.fraud.dto.FraudDataRequest;
import com.example.fraud.entity.FraudRecord;
import com.example.fraud.repository.FraudRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FraudService {
    
    private static final Logger logger = LoggerFactory.getLogger(FraudService.class);
    
    private final FraudRecordRepository fraudRecordRepository;
    
    public FraudService(FraudRecordRepository fraudRecordRepository) {
        this.fraudRecordRepository = fraudRecordRepository;
    }
    
    /**
     * Create a new fraud record from request data
     */
    public UUID createFraudRecord(FraudDataRequest request) {
        logger.info("Creating fraud record for user: {}, transaction: {}", 
                   request.getUserId(), request.getTransactionId());
        
        // Validate required fields
        validateFraudRequest(request);
        
        // Check if transaction already exists
        Optional<FraudRecord> existingRecord = fraudRecordRepository.findByTransactionId(request.getTransactionId());
        if (existingRecord.isPresent()) {
            logger.warn("Fraud record already exists for transaction: {}", request.getTransactionId());
            return existingRecord.get().getId();
        }
        
        // Create new fraud record
        FraudRecord fraudRecord = new FraudRecord(
            request.getUserId(),
            request.getTransactionId(),
            request.getAmount(),
            request.getCurrency(),
            request.getMerchantName(),
            request.getFraudType(),
            request.getDescription(),
            request.getRiskLevel(),
            request.getDetectedAt() != null ? request.getDetectedAt() : LocalDateTime.now()
        );
        
        // Set optional fields
        fraudRecord.setIpAddress(request.getIpAddress());
        fraudRecord.setLocation(request.getLocation());
        fraudRecord.setAdditionalInfo(request.getAdditionalInfo());
        
        // Save to database
        FraudRecord savedRecord = fraudRecordRepository.save(fraudRecord);
        
        logger.info("Fraud record created successfully with ID: {}", savedRecord.getId());
        return savedRecord.getId();
    }
    
    /**
     * Get fraud record by ID
     */
    @Transactional(readOnly = true)
    public Optional<FraudRecord> getFraudRecord(UUID id) {
        logger.debug("Retrieving fraud record with ID: {}", id);
        return fraudRecordRepository.findById(id);
    }
    
    /**
     * Get fraud records by user ID
     */
    @Transactional(readOnly = true)
    public List<FraudRecord> getFraudRecordsByUserId(String userId) {
        logger.debug("Retrieving fraud records for user: {}", userId);
        return fraudRecordRepository.findByUserId(userId);
    }
    
    /**
     * Get fraud record by transaction ID
     */
    @Transactional(readOnly = true)
    public Optional<FraudRecord> getFraudRecordByTransactionId(String transactionId) {
        logger.debug("Retrieving fraud record for transaction: {}", transactionId);
        return fraudRecordRepository.findByTransactionId(transactionId);
    }
    
    /**
     * Get fraud records by risk level
     */
    @Transactional(readOnly = true)
    public List<FraudRecord> getFraudRecordsByRiskLevel(String riskLevel) {
        logger.debug("Retrieving fraud records for risk level: {}", riskLevel);
        return fraudRecordRepository.findByRiskLevel(riskLevel);
    }
    
    /**
     * Get recent fraud records (last 30 days)
     */
    @Transactional(readOnly = true)
    public List<FraudRecord> getRecentFraudRecords() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        logger.debug("Retrieving fraud records from the last 30 days");
        return fraudRecordRepository.findRecentFraudRecords(thirtyDaysAgo);
    }
    
    /**
     * Get high-risk unverified fraud records
     */
    @Transactional(readOnly = true)
    public List<FraudRecord> getHighRiskUnverifiedRecords() {
        logger.debug("Retrieving high-risk unverified fraud records");
        return fraudRecordRepository.findHighRiskUnverifiedRecords();
    }
    
    /**
     * Update fraud record verification status
     */
    public void updateVerificationStatus(UUID id, boolean isVerified) {
        logger.info("Updating verification status for fraud record: {} to {}", id, isVerified);
        
        Optional<FraudRecord> recordOptional = fraudRecordRepository.findById(id);
        if (recordOptional.isPresent()) {
            FraudRecord record = recordOptional.get();
            record.setIsVerified(isVerified);
            fraudRecordRepository.save(record);
            logger.info("Verification status updated successfully");
        } else {
            logger.warn("Fraud record not found with ID: {}", id);
            throw new RuntimeException("Fraud record not found with ID: " + id);
        }
    }
    
    /**
     * Get fraud statistics
     */
    @Transactional(readOnly = true)
    public FraudStatistics getFraudStatistics() {
        logger.debug("Calculating fraud statistics");
        
        long totalRecords = fraudRecordRepository.count();
        long highRiskRecords = fraudRecordRepository.countByRiskLevel("HIGH");
        long mediumRiskRecords = fraudRecordRepository.countByRiskLevel("MEDIUM");
        long lowRiskRecords = fraudRecordRepository.countByRiskLevel("LOW");
        long unverifiedRecords = fraudRecordRepository.findByIsVerified(false).size();
        
        return new FraudStatistics(totalRecords, highRiskRecords, mediumRiskRecords, 
                                 lowRiskRecords, unverifiedRecords);
    }
    
    /**
     * Validate fraud request data
     */
    private void validateFraudRequest(FraudDataRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getTransactionId() == null || request.getTransactionId().trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (request.getMerchantName() == null || request.getMerchantName().trim().isEmpty()) {
            throw new IllegalArgumentException("Merchant name is required");
        }
        if (request.getFraudType() == null || request.getFraudType().trim().isEmpty()) {
            throw new IllegalArgumentException("Fraud type is required");
        }
        if (request.getRiskLevel() == null || request.getRiskLevel().trim().isEmpty()) {
            throw new IllegalArgumentException("Risk level is required");
        }
        
        // Validate risk level values
        String riskLevel = request.getRiskLevel().toUpperCase();
        if (!riskLevel.equals("HIGH") && !riskLevel.equals("MEDIUM") && !riskLevel.equals("LOW")) {
            throw new IllegalArgumentException("Risk level must be HIGH, MEDIUM, or LOW");
        }
        request.setRiskLevel(riskLevel);
    }
    
    /**
     * Inner class for fraud statistics
     */
    public static class FraudStatistics {
        private final long totalRecords;
        private final long highRiskRecords;
        private final long mediumRiskRecords;
        private final long lowRiskRecords;
        private final long unverifiedRecords;
        
        public FraudStatistics(long totalRecords, long highRiskRecords, long mediumRiskRecords, 
                              long lowRiskRecords, long unverifiedRecords) {
            this.totalRecords = totalRecords;
            this.highRiskRecords = highRiskRecords;
            this.mediumRiskRecords = mediumRiskRecords;
            this.lowRiskRecords = lowRiskRecords;
            this.unverifiedRecords = unverifiedRecords;
        }
        
        // Getters
        public long getTotalRecords() { return totalRecords; }
        public long getHighRiskRecords() { return highRiskRecords; }
        public long getMediumRiskRecords() { return mediumRiskRecords; }
        public long getLowRiskRecords() { return lowRiskRecords; }
        public long getUnverifiedRecords() { return unverifie
