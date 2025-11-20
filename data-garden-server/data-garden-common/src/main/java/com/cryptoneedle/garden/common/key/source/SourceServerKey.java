package com.cryptoneedle.garden.common.key.source;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: 数据源-服务器-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
public class SourceServerKey implements Serializable {

    private String host;
    private Integer port;
}