package com.cryptoneedle.garden.common.key.source;

import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: 数据源-维度-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
public class SourceDimensionColumnKey implements Serializable {

    private String catalog;
    private String database;
    private String table;
    private SourceDimensionType dimensionType;
    private String dimension;
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