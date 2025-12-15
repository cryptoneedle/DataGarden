package com.cryptoneedle.garden.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 模型类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-14
 */
@Getter
@AllArgsConstructor
public enum ModelTypes {
    
    CLAUDE_HAIKU_4_5("anthropic/claude-haiku-4.5"),
    CLAUDE_SONNET_4_5("anthropic/claude-sonnet-4.5"),
    DEEPSEEK_3_2("deepseek/deepseek-v3.2"),
    DEEPSEEK_3_2_SPECIALE("deepseek/deepseek-v3.2-speciale"),
    GEMINI_3_PRO_PREVIEW("google/gemini-3-pro-preview"),
    ;
    
    private final String model;
}