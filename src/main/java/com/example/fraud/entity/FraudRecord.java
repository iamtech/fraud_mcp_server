package com.example.fraud.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fraud_records")
public class FraudRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String transactionId;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private String currency;
    
    @Column(nullable = false)
    private String merchantName;
    
    @Column(nullable = false)
    private String fraudType;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String riskLevel;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime detectedAt;
    
    @Column
    private String ipAddress;
    
    @Column
    private String location;
    
    @Column
    private Boolean isVerified;
    
    @Column(length = 2000)
    private String additionalInfo;
    
    // Constructors
    public FraudRecord() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.isVerified = false;
    }
    
    public FraudRecord(String userId, String transactionId, Double amount, 
                      String currency, String merchantName, String fraudType, 
                      String description, String riskLevel, LocalDateTime detectedAt) {
        this();
        this.userId = userId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.merchantName = merchantName;
        this.fraudType = fraudType;
        this.description = description;
        this.riskLevel = riskLevel;
        this.detectedAt = detectedAt;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getMerchantName() {
        return merchantName;
    }
    
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    
    public String getFraudType() {
        return fraudType;
    }
    
    public void setFraudType(String fraudType) {
        this.fraudType = fraudType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }
    
    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    @Override
    public String toString() {
        return "FraudRecord{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", fraudType='" + fraudType + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", createdAt=" + createdAt +
                ", detectedAt=" + detectedAt +
                '}';
    }
}
