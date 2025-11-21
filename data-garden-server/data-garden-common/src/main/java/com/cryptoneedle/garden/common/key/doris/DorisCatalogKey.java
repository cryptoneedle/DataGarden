package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: Doris数据库-目录-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
@Schema(description = "Doris数据库-目录-主键")
public class DorisCatalogKey implements Serializable {

    @Column(name = "\"catalog\"", length = 512)
    @Schema(description = "目录")
    private String catalog;
}