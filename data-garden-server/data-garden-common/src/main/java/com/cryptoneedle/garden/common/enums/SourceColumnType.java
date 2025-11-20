package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 数据源-字段类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum SourceColumnType {

    UNIQUE("主键字段"),
    COMMON("普通字段"),
    ;

    private final String description;
}