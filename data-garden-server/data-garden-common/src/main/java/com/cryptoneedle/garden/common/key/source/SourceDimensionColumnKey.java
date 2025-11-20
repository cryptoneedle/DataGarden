package com.cryptoneedle.garden.common.key.source;

import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: 数据源-维度字段-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
@Schema(description = "数据源-维度字段-主键")
public class SourceDimensionColumnKey implements Serializable {

    @Schema(description = "目录")
    private String catalog;

    @Schema(description = "数据库")
    private String database;

    @Schema(description = "表")
    private String table;

    @Schema(description = "维度类型")
    private SourceDimensionType dimensionType;

    @Schema(description = "维度")
    private String dimension;

    @Schema(description = "字段")
    private String column;

    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalog(this.catalog).build();
    }

    public SourceDatabaseKey sourceDatabaseKey() {
        return SourceDatabaseKey.builder().catalog(this.catalog).database(this.database).build();
    }

    public SourceTableKey sourceTableKey() {
        return SourceTableKey.builder().catalog(this.catalog).database(this.database).table(this.table).build();
    }

    public SourceDimensionKey sourceDimensionKey() {
        return SourceDimensionKey.builder().catalog(this.catalog).database(this.database).table(this.table).dimensionType(this.dimensionType).dimension(this.dimension).build();
    }

    public SourceColumnKey sourceColumnKey() {
        return SourceColumnKey.builder().catalog(this.catalog).database(this.database).table(this.table).column(this.column).build();
    }
}