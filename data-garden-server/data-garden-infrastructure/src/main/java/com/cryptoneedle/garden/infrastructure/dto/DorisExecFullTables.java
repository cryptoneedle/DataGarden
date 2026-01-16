package com.cryptoneedle.garden.infrastructure.dto;

import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>description: Doris-元数据-Table </p>
 * <p>
 * 数据来源：SHOW FULL TABLES FROM internal.xxx;
 * <db_name>所在数据库下面所有的表以及视图
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
public class DorisExecFullTables {

    private DorisTableKey id;
    
    private String tableType;

    private String storageFormat;

    private String invertedIndexStorageFormat;
}