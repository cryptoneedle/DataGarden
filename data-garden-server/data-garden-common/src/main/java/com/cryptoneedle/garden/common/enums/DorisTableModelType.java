package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: DORIS-表模型类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum DorisTableModelType {

    UNIQUE_KEY("主键模型"),
    DUPLICATE_KEY("明细模型"),
    AGGREGATE_KEY("聚合模型"),
    ;

    private final String description;
}