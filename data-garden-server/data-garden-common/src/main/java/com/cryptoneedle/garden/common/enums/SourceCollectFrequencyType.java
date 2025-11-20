package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 数据源-采集频率 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum SourceCollectFrequencyType {

    DAY("每一天"),
    HOUR(" 每小时"),
    FIVE_MINUTE("每五分钟"),
    ;

    private final String description;
}