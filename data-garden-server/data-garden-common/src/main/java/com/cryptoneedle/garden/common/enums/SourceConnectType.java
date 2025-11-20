package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 数据源-连接类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum SourceConnectType {

    DIRECT("直连"),
    SID("Oracle-SID"),
    SERVICE_NAME("Oracle-ServiceName"),
    ;

    private final String description;
}