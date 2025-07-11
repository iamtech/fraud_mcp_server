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
            .description("Create a new fraud record with
