package com.cryptoneedle.garden.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>description: 数据源-数据库类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum SourceDatabaseType {

    ORACLE("Oracle"),
    MYSQL("MySQL"),
    POSTGRESQL("PostgreSQL"),
    SQLSERVER("SQL Server"),
    ;

    private final String description;
}