package com.cryptoneedle.garden.common.key.source;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: 数据源-数据库-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
@Schema(description = "数据源-数据库-主键")
public class SourceDatabaseKey implements Serializable {

    @Column(name = "\"catalog\"")
    @Schema(description = "目录")
    private String catalog;

    @Column(name = "\"database\"")
    @Schema(description = "数据库")
    private String database;

    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalog(this.catalog).build();
    }
}