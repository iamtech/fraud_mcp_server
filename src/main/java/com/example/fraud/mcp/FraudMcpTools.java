package com.example.fraud.mcp;

import com.example.fraud.dto.FraudDataRequest;
import com.example.fraud.entity.FraudRecord;
import com.example.fraud.service.FraudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.server.McpTool;
import org.springframework.ai.mcp.server.McpToolFunction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class FraudMcpTools {
    
    private static final Logger logger = LoggerFactory.getLogger(FraudMcpTools.class);
    
    private final FraudService fraudService;
    private final ObjectMapper objectMapper;
    
    public FraudMcpTools(FraudService fraudService, ObjectMapper objectMapper) {
        this.fraudService = fraudService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * MCP Tool to create a fraud record
     */
    @McpTool(name = "create_fraud_record", description = "Create a new fraud record in the database")
    public McpToolFunction createFraudRecord() {
        return McpToolFunction.builder()
            .name("create_fraud_record")
            .description("Create a new fraud record with the provided fraud data")
            .parameters(Map.of(
                "user_id", Map.of("type", "string", "description", "User ID associated with the fraud"),
                "transaction_id", Map.of("type", "string", "description", "Transaction ID of the fraudulent transaction"),
                "amount", Map.of("type", "number", "description", "Amount involved in the fraud"),
                "currency", Map.of("type", "string", "description", "Currency of the transaction"),
                "merchant_name", Map.of("type", "string", "description", "Name of the merchant"),
                "fraud_type", Map.of("type", "string", "description", "Type of fraud (e.g., credit_card_fraud, identity_theft)"),
                "description", Map.of("type", "string", "description", "Description of the fraud incident"),
                "risk_level", Map.of("type", "string", "description", "Risk level: HIGH, MEDIUM, or LOW"),
                "detected_at", Map.of("type", "string", "description", "Detection timestamp (ISO format)", "required", false),
                "ip_address", Map.of("type", "string", "description", "IP address of the fraudster", "required", false),
                "location", Map.of("type", "string", "description", "Geographic location", "required", false),
                "additional_info", Map.of("type", "string", "description", "Additional information", "required", false)
            ))
            .function(args -> {
                try {
                    logger.info("Creating fraud record with args: {}", args);
                    
                    // Convert args to FraudDataRequest
                    FraudDataRequest request = new FraudDataRequest();
                    request.setUserId((String) args.get("user_id"));
                    request.setTransactionId((String) args.get("transaction_id"));
                    request.setAmount(Double.valueOf(args.get("amount").toString()));
                    request.setCurrency((String) args.get("currency"));
                    request.setMerchantName((String) args.get("merchant_name"));
                    request.setFraudType((String) args.get("fraud_type"));
                    request.setDescription((String) args.get("description"));
                    request.setRiskLevel((String) args.get("risk_level"));
                    
                    // Handle optional fields
                    if (args.containsKey("detected_at") && args.get("detected_at") != null) {
                        request.setDetectedAt(LocalDateTime.parse((String) args.get("detected_at")));
                    }
                    if (args.containsKey("ip_address")) {
                        request.setIpAddress((String) args.get("ip_address"));
                    }
                    if (args.containsKey("location")) {
                        request.setLocation((String) args.get("location"));
                    }
                    if (args.containsKey("additional_info")) {
                        request.setAdditionalInfo((String) args.get("additional_info"));
                    }
                    
                    // Create fraud record
                    UUID fraudRecordId = fraudService.createFraudRecord(request);
                    
                    // Return response
                    return Map.of(
                        "success", true,
                        "reference_id", fraudRecordId.toString(),
                        "message", "Fraud record created successfully",
                        "created_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    );
                    
                } catch (Exception e) {
                    logger.error("Error creating fraud record: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to create fraud record"
                    );
                }
            })
            .build();
    }
    
    /**
     * MCP Tool to get fraud record by ID
     */
    @McpTool(name = "get_fraud_record", description = "Get a fraud record by its ID")
    public McpToolFunction getFraudRecord() {
        return McpToolFunction.builder()
            .name("get_fraud_record")
            .description("Retrieve a fraud record by its reference ID")
            .parameters(Map.of(
                "reference_id", Map.of("type", "string", "description", "Reference ID of the fraud record")
            ))
            .function(args -> {
                try {
                    String referenceId = (String) args.get("reference_id");
                    UUID fraudRecordId = UUID.fromString(referenceId);
                    
                    Optional<FraudRecord> fraudRecord = fraudService.getFraudRecord(fraudRecordId);
                    
                    if (fraudRecord.isPresent()) {
                        FraudRecord record = fraudRecord.get();
                        return Map.of(
                            "success", true,
                            "fraud_record", Map.of(
                                "id", record.getId().toString(),
                                "user_id", record.getUserId(),
                                "transaction_id", record.getTransactionId(),
                                "amount", record.getAmount(),
                                "currency", record.getCurrency(),
                                "merchant_name", record.getMerchantName(),
                                "fraud_type", record.getFraudType(),
                                "description", record.getDescription(),
                                "risk_level", record.getRiskLevel(),
                                "created_at", record.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                "detected_at", record.getDetectedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                "ip_address", record.getIpAddress(),
                                "location", record.getLocation(),
                                "is_verified", record.getIsVerified(),
                                "additional_info", record.getAdditionalInfo()
                            )
                        );
                    } else {
                        return Map.of(
                            "success", false,
                            "message", "Fraud record not found with ID: " + referenceId
                        );
                    }
                    
                } catch (Exception e) {
                    logger.error("Error retrieving fraud record: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to retrieve fraud record"
                    );
                }
            })
            .build();
    }
    
    /**
     * MCP Tool to get fraud records by user ID
     */
    @McpTool(name = "get_user_fraud_records", description = "Get all fraud records for a specific user")
    public McpToolFunction getUserFraudRecords() {
        return McpToolFunction.builder()
            .name("get_user_fraud_records")
            .description("Retrieve all fraud records associated with a specific user ID")
            .parameters(Map.of(
                "user_id", Map.of("type", "string", "description", "User ID to search for")
            ))
            .function(args -> {
                try {
                    String userId = (String) args.get("user_id");
                    List<FraudRecord> fraudRecords = fraudService.getFraudRecordsByUserId(userId);
                    
                    return Map.of(
                        "success", true,
                        "user_id", userId,
                        "total_records", fraudRecords.size(),
                        "fraud_records", fraudRecords.stream().map(record -> Map.of(
                            "id", record.getId().toString(),
                            "transaction_id", record.getTransactionId(),
                            "amount", record.getAmount(),
                            "currency", record.getCurrency(),
                            "merchant_name", record.getMerchantName(),
                            "fraud_type", record.getFraudType(),
                            "risk_level", record.getRiskLevel(),
                            "created_at", record.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            "is_verified", record.getIsVerified()
                        )).toList()
                    );
                    
                } catch (Exception e) {
                    logger.error("Error retrieving user fraud records: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to retrieve user fraud records"
                    );
                }
            })
            .build();
    }
    
    /**
     * MCP Tool to get fraud statistics
     */
    @McpTool(name = "get_fraud_statistics", description = "Get fraud statistics and summary")
    public McpToolFunction getFraudStatistics() {
        return McpToolFunction.builder()
            .name("get_fraud_statistics")
            .description("Retrieve fraud statistics including total records, risk levels, and verification status")
            .parameters(Map.of()) // No parameters needed
            .function(args -> {
                try {
                    FraudService.FraudStatistics stats = fraudService.getFraudStatistics();
                    
                    return Map.of(
                        "success", true,
                        "statistics", Map.of(
                            "total_records", stats.getTotalRecords(),
                            "high_risk_records", stats.getHighRiskRecords(),
                            "medium_risk_records", stats.getMediumRiskRecords(),
                            "low_risk_records", stats.getLowRiskRecords(),
                            "unverified_records", stats.getUnverifiedRecords(),
                            "verified_records", stats.getTotalRecords() - stats.getUnverifiedRecords()
                        ),
                        "generated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    );
                    
                } catch (Exception e) {
                    logger.error("Error retrieving fraud statistics: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to retrieve fraud statistics"
                    );
                }
            })
            .build();
    }
    
    /**
     * MCP Tool to get recent fraud records
     */
    @McpTool(name = "get_recent_fraud_records", description = "Get recent fraud records from the last 30 days")
    public McpToolFunction getRecentFraudRecords() {
        return McpToolFunction.builder()
            .name("get_recent_fraud_records")
            .description("Retrieve fraud records from the last 30 days")
            .parameters(Map.of()) // No parameters needed
            .function(args -> {
                try {
                    List<FraudRecord> recentRecords = fraudService.getRecentFraudRecords();
                    
                    return Map.of(
                        "success", true,
                        "total_records", recentRecords.size(),
                        "period", "Last 30 days",
                        "fraud_records", recentRecords.stream().map(record -> Map.of(
                            "id", record.getId().toString(),
                            "user_id", record.getUserId(),
                            "transaction_id", record.getTransactionId(),
                            "amount", record.getAmount(),
                            "currency", record.getCurrency(),
                            "merchant_name", record.getMerchantName(),
                            "fraud_type", record.getFraudType(),
                            "risk_level", record.getRiskLevel(),
                            "created_at", record.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            "is_verified", record.getIsVerified()
                        )).toList()
                    );
                    
                } catch (Exception e) {
                    logger.error("Error retrieving recent fraud records: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to retrieve recent fraud records"
                    );
                }
            })
            .build();
    }
}
