package com.example.fraud.config;

import com.example.fraud.mcp.FraudMcpTools;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfiguration {
    
    private final FraudMcpTools fraudMcpTools;
    
    public McpConfiguration(FraudMcpTools fraudMcpTools) {
        this.fraudMcpTools = fraudMcpTools;
    }
    
    @Bean
    public McpServer mcpServer() {
        return McpServer.builder()
            .config(mcpServerConfig())
            .tools(
                fraudMcpTools.createFraudRecord(),
                fraudMcpTools.getFraudRecord(),
                fraudMcpTools.getUserFraudRecords(),
                fraudMcpTools.getFraudStatistics(),
                fraudMcpTools.getRecentFraudRecords()
            )
            .build();
    }
    
    @Bean
    public McpServerConfig mcpServerConfig() {
        return McpServerConfig.builder()
            .name("fraud-detection-server")
            .version("1.0.0")
            .description("MCP Server for Fraud Detection and Management")
            .build();
    }
}
