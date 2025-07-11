package com.example.fraud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class FraudMcpServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FraudMcpServerApplication.class, args);
    }
    
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
