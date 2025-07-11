package com.example.fraud.config;

import com.example.fraud.mcp.EnhancedFraudMcpTools;
import com.example.fraud.mcp.FraudMcpTools;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfiguration {
    
    private final FraudMcpTools fraudMcpTools;
    private final EnhancedFraudMcpTools enhancedFraudMcpTools;
    
    public McpConfiguration(FraudMcpTools fraudMcpTools, EnhancedFraudMcpTools enhancedFraudMcpTools) {
        this.fraudMcpTools = fraudMcpTools;
        this.enhancedFraudMcpTools = enhancedFraudMcpTools;
    }
    
    @Bean
    public McpServer mcpServer() {
        return McpServer.builder()
            .config(mcpServerConfig())
            .tools(
                // Basic fraud tools
                fraudMcpTools.createFraudRecord(),
                fraudMcpTools.getFraudRecord(),
                fraudMcpTools.getUserFraudRecords(),
                fraudMcpTools.getFraudStatistics(),
                fraudMcpTools.getRecentFraudRecords(),
                
                // Enhanced AI-powered tools
                enhancedFraudMcpTools.createFraudRecordWithAi(),
                enhancedFraudMcpTools.analyzeFraudPatterns(),
                enhancedFraudMcpTools.generateUserRiskAssessment(),
                enhancedFraudMcpTools.getFraudPreventionTips(),
                enhancedFraudMcpTools.getFraudDashboard()
            )
            .build();
    }
    
    @Bean
    public McpServerConfig mcpServerConfig() {
        return McpServerConfig.builder()
            .name("fraud-detection-server")
            .version("1.0.0")
            .description("Enhanced MCP Server for Fraud Detection and Management with AI-powered insights using AWS Bedrock Claude")
            .build();
    }
}
