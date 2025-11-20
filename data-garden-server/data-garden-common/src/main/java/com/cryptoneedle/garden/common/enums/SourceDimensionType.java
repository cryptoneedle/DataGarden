package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 数据源-维度类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum SourceDimensionType {

    MANUAL("手动", 1),
    PRIMARY_CONSTRAINT("主键约束", 2),
    UNIQUE_CONSTRAINT("唯一键约束", 3),
    UNIQUE_INDEX("唯一索引", 4);
    ;

    private final String description;
    private int sort;
}