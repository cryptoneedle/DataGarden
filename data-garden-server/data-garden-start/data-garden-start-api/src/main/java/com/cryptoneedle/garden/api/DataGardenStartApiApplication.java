package com.cryptoneedle.garden.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>description: API 启动类 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@ComponentScan(basePackages = "com.cryptoneedle.garden")
public class DataGardenStartApiApplication {
    
    static void main() {
        SpringApplication.run(DataGardenStartApiApplication.class);
    }
}