package com.cryptoneedle.garden.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-05-07
 */
@Data
@Builder
public class DwdColumnGen {
    
    private String originTableName;
    
    private String originColumnName;
    
    private String columnName;
    
    private String columnComment;
    
    private String columnDataTypeFormat;
    
    private Boolean translated;
}