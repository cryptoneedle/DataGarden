package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 数据源-表类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum SourceTableType {

    TABLE("表"),
    VIEW("视图"),
    ;

    private final String description;
}