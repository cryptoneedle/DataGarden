package com.cryptoneedle.garden.config;

import com.cryptoneedle.garden.enums.ModelTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * <p>description: AI 配置类 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-15
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "data-garden.ai")
@Schema(description = "AI配置类")
public class AIProperties {
    
    @Schema(description = "连接地址")
    private String baseUrl;
    
    @Schema(description = "API Key")
    private String apiKey;
    
    @Schema(description = "受支持的模型")
    private List<ModelTypes> models;
}