package com.cryptoneedle.garden.infrastructure.dto;

import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class DorisExecShowData implements Serializable {
    
    private DorisTableKey id;
    
    // 占用空间
    private String size;

    // 副本数量
    private Integer replicaCount;

    // 远程存储数据量
    private String remoteSize;

    // 占用空间(Mb)
    private BigDecimal sizeFormatMegaBytes;
}