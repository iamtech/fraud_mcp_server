package com.example.fraud.mcp;

import com.example.fraud.dto.FraudDataRequest;
import com.example.fraud.entity.FraudRecord;
import com.example.fraud.service.FraudAiService;
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
public class EnhancedFraudMcpTools {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedFraudMcpTools.class);
    
    private final FraudService fraudService;
    private final FraudAiService fraudAiService;
    private final ObjectMapper objectMapper;
    
    public EnhancedFraudMcpTools(FraudService fraudService, FraudAiService fraudAiService, ObjectMapper objectMapper) {
        this.fraudService = fraudService;
        this.fraudAiService = fraudAiService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Enhanced MCP Tool to create a fraud record with AI-generated response
     */
    @McpTool(name = "create_fraud_record_with_ai", description = "Create a fraud record and generate AI-powered natural language response")
    public McpToolFunction createFraudRecordWithAi() {
        return McpToolFunction.builder()
            .name("create_fraud_record_with_ai")
            .description("Create a new fraud record with the provided fraud data and generate an AI-powered response")
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
                    logger.info("Creating fraud record with AI response for args: {}", args);
                    
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
                    
                    // Get the created fraud record
                    Optional<FraudRecord> fraudRecordOpt = fraudService.getFraudRecord(fraudRecordId);
                    
                    if (fraudRecordOpt.isPresent()) {
                        FraudRecord fraudRecord = fraudRecordOpt.get();
                        
                        // Generate AI response
                        String aiResponse = fraudAiService.generateFraudRecordResponse(fraudRecordId, fraudRecord);
                        
                        // Return comprehensive response
                        return Map.of(
                            "success", true,
                            "reference_id", fraudRecordId.toString(),
                            "fraud_record", Map.of(
                                "id", fraudRecord.getId().toString(),
                                "user_id", fraudRecord.getUserId(),
                                "transaction_id", fraudRecord.getTransactionId(),
                                "amount", fraudRecord.getAmount(),
                                "currency", fraudRecord.getCurrency(),
                                "merchant_name", fraudRecord.getMerchantName(),
                                "fraud_type", fraudRecord.getFraudType(),
                                "risk_level", fraudRecord.getRiskLevel(),
                                "created_at", fraudRecord.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            ),
                            "ai_response", aiResponse,
                            "message", "Fraud record created successfully with AI analysis"
                        );
                    } else {
                        return Map.of(
                            "success", false,
                            "message", "Fraud record created but could not be retrieved for AI analysis"
                        );
                    }
                    
                } catch (Exception e) {
                    logger.error("Error creating fraud record with AI: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to create fraud record with AI analysis"
                    );
                }
            })
            .build();
    }
    
    /**
     * MCP Tool to analyze fraud patterns with AI
     */
    @McpTool(name = "analyze_fraud_patterns", description = "Analyze fraud patterns using AI")
    public McpToolFunction analyzeFraudPatterns() {
        return McpToolFunction.builder()
            .name("analyze_fraud_patterns")
            .description("Analyze fraud patterns and trends using AI-powered analysis")
            .parameters(Map.of(
                "days", Map.of("type", "number", "description", "Number of days to analyze (default: 30)", "required", false),
                "risk_level", Map.of("type", "string", "description", "Filter by risk level (HIGH, MEDIUM, LOW)", "required", false),
                "fraud_type", Map.of("type", "string", "description", "Filter by fraud type", "required", false)
            ))
            .function(args -> {
                try {
                    logger.info("Analyzing fraud patterns with AI for args: {}", args);
                    
                    // Get recent fraud records (default 30 days)
                    List<FraudRecord> fraudRecords = fraudService.getRecentFraudRecords();
                    
                    // Apply filters if provided
                    if (args.containsKey("risk_level") && args.get("risk_level") != null) {
                        String riskLevel = (String) args.get("risk_level");
                        fraudRecords = fraudRecords.stream()
                            .filter(record -> record.getRiskLevel().equalsIgnoreCase(riskLevel))
                            .toList();
                    }
                    
                    if (args.containsKey("fraud_type") && args.get("fraud_type") != null) {
                        String fraudType = (String) args.get("fraud_type");
                        fraudRecords = fraudRecords.stream()
                            .filter(record -> record.getFraudType().equalsIgnoreCase(fraudType))
                            .toList();
                    }
                    
                    // Generate AI analysis
                    String aiAnalysis = fraudAiService.analyzeFraudPatterns(fraudRecords);
                    
                    return Map.of(
                        "success", true,
                        "total_records_analyzed", fraudRecords.size(),
                        "analysis_period", "Last 30 days",
                        "ai_analysis", aiAnalysis,
                        "generated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    );
                    
                } catch (Exception e) {
                    logger.error("Error analyzing fraud patterns: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to analyze fraud patterns"
                    );
                }
            })
            .build();
    }
    
    /**
     * MCP Tool to generate user risk assessment
     */
    @McpTool(name = "generate_user_risk_assessment", description = "Generate AI-powered risk assessment for a user")
    public McpToolFunction generateUserRiskAssessment() {
        return McpToolFunction.builder()
            .name("generate_user_risk_assessment")
            .description("Generate a comprehensive risk assessment for a specific user using AI analysis")
            .parameters(Map.of(
                "user_id", Map.of("type", "string", "description", "User ID to assess")
            ))
            .function(args -> {
                try {
                    String userId = (String) args.get("user_id");
                    logger.info("Generating risk assessment for user: {}", userId);
                    
                    // Get user's fraud records
                    List<FraudRecord> userFraudRecords = fraudService.getFraudRecordsByUserId(userId);
                    
                    // Generate AI risk assessment
                    String riskAssessment = fraudAiService.generateRiskAssessment(userId, userFraudRecords);
                    
                    // Calculate basic statistics
                    long highRiskCount = userFraudRecords.stream()
                        .filter(record -> "HIGH".equals(record.getRiskLevel()))
                        .count();
                    
                    long mediumRiskCount = userFraudRecords.stream()
                        .filter(record -> "MEDIUM".equals(record.getRiskLevel()))
                        .count();
                    
                    long lowRiskCount = userFraudRecords.stream()
                        .filter(record -> "LOW".equals(record.getRiskLevel()))
                        .count();
                    
                    double totalAmount = userFraudRecords.stream()
                        .mapToDouble(FraudRecord::getAmount)
                        .sum();
                    
                    return Map.of(
                        "success", true,
                        "user_id", userId,
                        "risk_assessment", riskAssessment,
                        "statistics", Map.of(
                            "total_fraud_incidents", userFraudRecords.size(),
                            "high_risk_incidents", highRiskCount,
                            "medium_risk_incidents", mediumRiskCount,
                            "low_risk_incidents", lowRiskCount,
                            "total_fraud_amount", totalAmount
                        ),
                        "generated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    );
                    
                } catch (Exception e) {
                    logger.error("Error generating user risk assessment: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to generate user risk assessment"
                    );
                }
            })
            .build();
    }
    
    /**
     * MCP Tool to get fraud prevention recommendations
     */
    @McpTool(name = "get_fraud_prevention_tips", description = "Get AI-powered fraud prevention recommendations")
    public McpToolFunction getFraudPreventionTips() {
        return McpToolFunction.builder()
            .name("get_fraud_prevention_tips")
            .description("Get personalized fraud prevention tips and recommendations using AI")
            .parameters(Map.of(
                "fraud_type", Map.of("type", "string", "description", "Type of fraud to get prevention tips for"),
                "risk_level", Map.of("type", "string", "description", "Risk level (HIGH, MEDIUM, LOW)")
            ))
            .function(args -> {
                try {
                    String fraudType = (String) args.get("fraud_type");
                    String riskLevel = (String) args.get("risk_level");
                    
                    logger.info("Generating fraud prevention tips for type: {}, risk: {}", fraudType, riskLevel);
                    
                    // Generate AI-powered prevention tips
                    String preventionTips = fraudAiService.generateFraudPreventionTips(fraudType, riskLevel);
                    
                    return Map.of(
                        "success", true,
                        "fraud_type", fraudType,
                        "risk_level", riskLevel,
                        "prevention_tips", preventionTips,
                        "generated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    );
                    
                } catch (Exception e) {
                    logger.error("Error generating fraud prevention tips: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to generate fraud prevention tips"
                    );
                }
            })
            .build();
    }
    
    /**
     * MCP Tool to get comprehensive fraud dashboard data
     */
    @McpTool(name = "get_fraud_dashboard", description = "Get comprehensive fraud dashboard with AI insights")
    public McpToolFunction getFraudDashboard() {
        return McpToolFunction.builder()
            .name("get_fraud_dashboard")
            .description("Get a comprehensive fraud dashboard with statistics and AI-powered insights")
            .parameters(Map.of()) // No parameters needed
            .function(args -> {
                try {
                    logger.info("Generating fraud dashboard with AI insights");
                    
                    // Get basic statistics
                    FraudService.FraudStatistics stats = fraudService.getFraudStatistics();
                    
                    // Get recent fraud records for analysis
                    List<FraudRecord> recentRecords = fraudService.getRecentFraudRecords();
                    
                    // Get high-risk unverified records
                    List<FraudRecord> highRiskUnverified = fraudService.getHighRiskUnverifiedRecords();
                    
                    // Generate AI insights
                    String aiInsights = fraudAiService.analyzeFraudPatterns(recentRecords);
                    
                    return Map.of(
                        "success", true,
                        "dashboard_data", Map.of(
                            "statistics", Map.of(
                                "total_records", stats.getTotalRecords(),
                                "high_risk_records", stats.getHighRiskRecords(),
                                "medium_risk_records", stats.getMediumRiskRecords(),
                                "low_risk_records", stats.getLowRiskRecords(),
                                "unverified_records", stats.getUnverifiedRecords(),
                                "verified_records", stats.getTotalRecords() - stats.getUnverifiedRecords()
                            ),
                            "recent_activity", Map.of(
                                "last_30_days", recentRecords.size(),
                                "high_risk_unverified", highRiskUnverified.size()
                            ),
                            "ai_insights", aiInsights
                        ),
                        "generated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    );
                    
                } catch (Exception e) {
                    logger.error("Error generating fraud dashboard: {}", e.getMessage(), e);
                    return Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "message", "Failed to generate fraud dashboard"
                    );
                }
            })
            .build();
    }
}
