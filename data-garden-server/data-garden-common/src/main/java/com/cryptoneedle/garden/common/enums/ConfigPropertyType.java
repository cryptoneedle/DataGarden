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
public enum ConfigPropertyType {

    AUTO("自动配置"),
    MANUAL("手动配置"),
    ;

    private final String description;
}