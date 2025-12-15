package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: DORIS-目录类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum DorisCatalogType {
    
    INTERNAL("内部目录"),
    EXTERN("外部目录"),
    ;
    
    private final String description;
}