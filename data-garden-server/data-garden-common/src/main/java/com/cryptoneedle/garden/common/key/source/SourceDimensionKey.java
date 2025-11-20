package com.cryptoneedle.garden.common.key.source;

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
public class SourceDimensionKey implements Serializable {

    private String catalog;
    private String database;
    private String table;
    private String dimension;

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