package com.cryptoneedle.garden.infrastructure.dto;

import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: Doris-元数据-TableStatus </p>
 * <p>
 * 数据来源：SHOW TABLE STATUS FROM internal.xxx;
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
public class DorisExecShowTableStatus implements Serializable {
    
    private DorisTableKey id;
    
    // 表存储引擎
    private String engine;

    // 无效值
    //private Long version;

    // 无效值
    //private String rowFormat;

    // 表预估行数
    private Long rows;

    // 平均每行包括的字节数
    private Long avgRowLength;

    // 整个表的数据量 (单位：字节)
    private Long dataLength;

    // 无效值
    //private Long maxDataLength;

    // 无效值
    //private Long indexLength;

    // 无效值
    //private Long dataFree;

    // 无效值
    //private Long autoIncrement;

    // 表创建时间
    private String createTime;

    // 表数据更新时间
    private String updateTime;

    // 无效值
    //private Date checkTime;

    // 固定值：utf-8
    private String tableCollation;

    // 无效值
    //private Long checksum;

    // 无效值
    //private String createOptions;

    // 表注释
    private String tableComment;
}