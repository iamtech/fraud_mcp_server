package com.example.fraud.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class FraudDataRequest {
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("transaction_id")
    private String transactionId;
    
    @JsonProperty("amount")
    private Double amount;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("merchant_name")
    private String merchantName;
    
    @JsonProperty("fraud_type")
    private String fraudType;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("risk_level")
    private String riskLevel;
    
    @JsonProperty("detected_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime detectedAt;
    
    @JsonProperty("ip_address")
    private String ipAddress;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("additional_info")
    private String additionalInfo;
    
    // Constructors
    public FraudDataRequest() {
    }
    
    public FraudDataRequest(String userId, String transactionId, Double amount, 
                           String currency, String merchantName, String fraudType, 
                           String description, String riskLevel, LocalDateTime detectedAt) {
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
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    @Override
    public String toString() {
        return "FraudDataRequest{" +
                "userId='" + userId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", fraudType='" + fraudType + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", detectedAt=" + detectedAt +
                '}';
    }
}
