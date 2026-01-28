package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-23
 */
@Getter
@AllArgsConstructor
public enum SourceTimeType {
    
    YYYYMMDD("YYYYMMDD"),
    YYYY_MM_DD("YYYY-MM-DD"),
    YYYYMMDDHHMMSS("YYYYMMDDhhmmss"),
    YYYY_MM_DD_HH_MM_SS("YYYY-MM-DD hh:mm:ss"),
    ;
    
    private final String description;
}