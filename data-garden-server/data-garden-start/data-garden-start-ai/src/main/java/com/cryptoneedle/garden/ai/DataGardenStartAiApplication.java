package com.cryptoneedle.garden.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * <p>description: AI 启动类 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-13
 */
@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = "com.cryptoneedle.garden")
public class DataGardenStartAiApplication {

    static void main() {
        SpringApplication.run(DataGardenStartAiApplication.class);
    }
    
}