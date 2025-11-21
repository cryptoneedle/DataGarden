package com.cryptoneedle.garden.common.key.source;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: 数据源-表-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
@Schema(description = "数据源-表-主键")
public class SourceTableKey implements Serializable {

    @Column(name = "\"catalog\"")
    @Schema(description = "目录")
    private String catalog;

    @Column(name = "\"database\"")
    @Schema(description = "数据库")
    private String database;

    @Column(name = "\"table\"")
    @Schema(description = "表")
    private String table;

    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalog(this.catalog).build();
    }

    public SourceDatabaseKey sourceDatabaseKey() {
        return SourceDatabaseKey.builder().catalog(this.catalog).database(this.database).build();
    }

    public SourceTableKey sourceTableKey() {
        return SourceTableKey.builder().catalog(this.catalog).database(this.database).table(this.table).build();
    }
}