package com.example.fraud.service;

import com.example.fraud.entity.FraudRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.bedrock.claude.BedrockClaudeChatModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FraudAiService {
    
    private static final Logger logger = LoggerFactory.getLogger(FraudAiService.class);
    
    private final BedrockClaudeChatModel chatModel;
    
    public FraudAiService(BedrockClaudeChatModel chatModel) {
        this.chatModel = chatModel;
    }
    
    /**
     * Generate a natural language response for fraud record creation
     */
    public String generateFraudRecordResponse(UUID referenceId, FraudRecord fraudRecord) {
        try {
            logger.info("Generating AI response for fraud record: {}", referenceId);
            
            String systemPrompt = """
                You are a fraud detection expert assistant. Your role is to provide clear, professional, 
                and helpful responses about fraud incidents. When a fraud record is created, you should:
                
                1. Acknowledge the fraud incident has been recorded
                2. Provide the reference ID for tracking
                3. Explain the risk level and what it means
                4. Suggest next steps or recommendations
                5. Be empathetic and professional in tone
                
                Keep responses concise but informative, around 2-3 paragraphs.
                """;
            
            String userPrompt = String.format("""
                A new fraud record has been created with the following details:
                
                Reference ID: %s
                User ID: %s
                Transaction ID: %s
                Amount: %.2f %s
                Merchant: %s
                Fraud Type: %s
                Risk Level: %s
                Description: %s
                Detection Time: %s
                
                Please provide a natural language response to inform the user about this fraud incident.
                """,
                referenceId.toString(),
                fraudRecord.getUserId(),
                fraudRecord.getTransactionId(),
                fraudRecord.getAmount(),
                fraudRecord.getCurrency(),
                fraudRecord.getMerchantName(),
                fraudRecord.getFraudType(),
                fraudRecord.getRiskLevel(),
                fraudRecord.getDescription(),
                fraudRecord.getDetectedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            
            List<Message> messages = List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userPrompt)
            );
            
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatModel.call(prompt);
            
            String aiResponse = response.getResult().getOutput().getContent();
            logger.info("AI response generated successfully");
            
            return aiResponse;
            
        } catch (Exception e) {
            logger.error("Error generating AI response: {}", e.getMessage(), e);
            return generateFallbackResponse(referenceId, fraudRecord);
        }
    }
    
    /**
     * Generate AI analysis and recommendations for fraud patterns
     */
    public String analyzeFraudPatterns(List<FraudRecord> fraudRecords) {
        try {
            logger.info("Analyzing fraud patterns for {} records", fraudRecords.size());
            
            if (fraudRecords.isEmpty()) {
                return "No fraud records available for analysis.";
            }
            
            String systemPrompt = """
                You are a fraud analyst expert. Analyze the provided fraud data and provide insights including:
                
                1. Common fraud patterns and trends
                2. Risk assessment and distribution
                3. Merchant or transaction patterns
                4. Recommendations for fraud prevention
                5. Any concerning trends or anomalies
                
                Be analytical and provide actionable insights.
                """;
            
            StringBuilder fraudDataBuilder = new StringBuilder();
            fraudDataBuilder.append("Fraud Records Analysis:\n\n");
            
            for (FraudRecord record : fraudRecords) {
                fraudDataBuilder.append(String.format("""
                    Record ID: %s
                    User: %s | Transaction: %s
                    Amount: %.2f %s | Merchant: %s
                    Type: %s | Risk: %s
                    Date: %s
                    ---
                    """,
                    record.getId().toString(),
                    record.getUserId(),
                    record.getTransactionId(),
                    record.getAmount(),
                    record.getCurrency(),
                    record.getMerchantName(),
                    record.getFraudType(),
                    record.getRiskLevel(),
                    record.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ));
            }
            
            List<Message> messages = List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(fraudDataBuilder.toString())
            );
            
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatModel.call(prompt);
            
            String analysis = response.getResult().getOutput().getContent();
            logger.info("Fraud pattern analysis generated successfully");
            
            return analysis;
            
        } catch (Exception e) {
            logger.error("Error analyzing fraud patterns: {}", e.getMessage(), e);
            return "Unable to analyze fraud patterns at this time. Please try again later.";
        }
    }
    
    /**
     * Generate fraud risk assessment
     */
    public String generateRiskAssessment(String userId, List<FraudRecord> userFraudRecords) {
        try {
            logger.info("Generating risk assessment for user: {}", userId);
            
            String systemPrompt = """
                You are a risk assessment specialist. Based on the user's fraud history, provide:
                
                1. Overall risk profile assessment
                2. Risk factors and concerns
                3. Recommendations for account security
                4. Monitoring suggestions
                5. Preventive measures
                
                Be professional and provide actionable advice.
                """;
            
            StringBuilder userDataBuilder = new StringBuilder();
            userDataBuilder.append(String.format("Risk Assessment for User: %s\n\n", userId));
            userDataBuilder.append(String.format("Total Fraud Incidents: %d\n\n", userFraudRecords.size()));
            
            if (!userFraudRecords.isEmpty()) {
                userDataBuilder.append("Fraud History:\n");
                for (FraudRecord record : userFraudRecords) {
                    userDataBuilder.append(String.format("""
                        - %s: %.2f %s at %s (Risk: %s)
                        """,
                        record.getFraudType(),
                        record.getAmount(),
                        record.getCurrency(),
                        record.getMerchantName(),
                        record.getRiskLevel()
                    ));
                }
            }
            
            List<Message> messages = List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userDataBuilder.toString())
            );
            
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatModel.call(prompt);
            
            String assessment = response.getResult().getOutput().getContent();
            logger.info("Risk assessment generated successfully");
            
            return assessment;
            
        } catch (Exception e) {
            logger.error("Error generating risk assessment: {}", e.getMessage(), e);
            return "Unable to generate risk assessment at this time. Please try again later.";
        }
    }
    
    /**
     * Generate fraud prevention recommendations
     */
    public String generateFraudPreventionTips(String fraudType, String riskLevel) {
        try {
            logger.info("Generating fraud prevention tips for type: {}, risk: {}", fraudType, riskLevel);
            
            String systemPrompt = """
                You are a fraud prevention expert. Provide specific, actionable fraud prevention tips based on:
                
                1. The specific fraud type
                2. The risk level
                3. Best practices for prevention
                4. Warning signs to watch for
                5. Immediate actions to take
                
                Make recommendations practical and easy to understand.
                """;
            
            String userPrompt = String.format("""
                Please provide fraud prevention recommendations for:
                
                Fraud Type: %s
                Risk Level: %s
                
                Focus on practical steps the user can take to prevent this type of fraud in the future.
                """, fraudType, riskLevel);
            
            List<Message> messages = List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userPrompt)
            );
            
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatModel.call(prompt);
            
            String tips = response.getResult().getOutput().getContent();
            logger.info("Fraud prevention tips generated successfully");
            
            return tips;
            
        } catch (Exception e) {
            logger.error("Error generating fraud prevention tips: {}", e.getMessage(), e);
            return "Unable to generate fraud prevention tips at this time. Please try again later.";
        }
    }
    
    /**
     * Generate a fallback response when AI is unavailable
     */
    private String generateFallbackResponse(UUID referenceId, FraudRecord fraudRecord) {
        return String.format("""
            Fraud Incident Recorded
            
            Your fraud report has been successfully recorded in our system with reference ID: %s
            
            Details:
            - Transaction ID: %s
            - Amount: %.2f %s
            - Merchant: %s
            - Risk Level: %s
            - Fraud Type: %s
            
            %s
            
            Please keep this reference ID for your records. Our fraud investigation team will review this incident.
            """,
            referenceId.toString(),
            fraudRecord.getTransactionId(),
            fraudRecord.getAmount(),
            fraudRecord.getCurrency(),
            fraudRecord.getMerchantName(),
            fraudRecord.getRiskLevel(),
            fraudRecord.getFraudType(),
            getRiskLevelMessage(fraudRecord.getRiskLevel())
        );
    }
    
    /**
     * Get risk level specific message
     */
    private String getRiskLevelMessage(String riskLevel) {
        return switch (riskLevel) {
            case "HIGH" -> "This is a high-risk incident that requires immediate attention. Please contact your bank immediately.";
            case "MEDIUM" -> "This is a medium-risk incident. Please monitor your accounts closely and consider additional security measures.";
            case "LOW" -> "This is a low-risk incident. Continue monitoring your accounts and practice good security habits.";
            default -> "Please monitor your accounts and take appropriate security measures.";
        };
    }
}
