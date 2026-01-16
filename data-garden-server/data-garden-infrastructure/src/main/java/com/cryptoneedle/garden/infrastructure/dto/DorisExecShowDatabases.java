package com.cryptoneedle.garden.infrastructure.dto;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>description: Doris-元数据-Database </p>
 * <p>
 * 数据来源：SHOW DATABASES FROM internal;
 *
 * @author CryptoNeedle
 * @date 2025-09-25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class DorisExecShowDatabases {

    private String catalogName;

    // 数据库名称
    private String databaseName;
}