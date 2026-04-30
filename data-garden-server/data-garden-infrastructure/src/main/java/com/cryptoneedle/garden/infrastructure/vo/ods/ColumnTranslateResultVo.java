package com.cryptoneedle.garden.infrastructure.vo.ods;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnTranslateResultVo {
    
    @JsonPropertyDescription("字段翻译列表")
    private List<ColumnTranslateResult> results;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnTranslateResult {
        
        @JsonPropertyDescription("字段值")
        private String value;
        
        @JsonPropertyDescription("字段翻译值")
        private String translateValue;
    }
}