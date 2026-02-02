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
    YYYY_MM_DD_HH_MM_SS_S1("YYYY-MM-DD hh:mm:ss.S"),
    YYYY_MM_DD_HH_MM_SS_S2("YYYY-MM-DD hh:mm:ss.SS"),
    YYYY_MM_DD_HH_MM_SS_S3("YYYY-MM-DD hh:mm:ss.SSS"),
    YYYY_MM_DD_HH_MM_SS_S4("YYYY-MM-DD hh:mm:ss.SSSS"),
    YYYY_MM_DD_HH_MM_SS_S5("YYYY-MM-DD hh:mm:ss.SSSSS"),
    YYYY_MM_DD_HH_MM_SS_S6("YYYY-MM-DD hh:mm:ss.SSSSSS"),
    ;
    
    private final String description;
}