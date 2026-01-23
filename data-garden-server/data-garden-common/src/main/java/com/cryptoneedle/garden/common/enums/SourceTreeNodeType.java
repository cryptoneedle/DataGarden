package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2025-07-25
 */
@Getter
@AllArgsConstructor
public enum SourceTreeNodeType {
    CATALOG(),
    DATABASE(),
    TABLE(),
    ;
}