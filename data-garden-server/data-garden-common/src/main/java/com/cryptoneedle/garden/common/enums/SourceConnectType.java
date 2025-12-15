package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 数据源-连接方式 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum SourceConnectType {
    
    DIRECT("直接连接"),
    SID("ORACLE_SID"),
    SERVICE_NAME("ORACLE_SERVICE_NAME"),
    ;
    
    private final String description;
}