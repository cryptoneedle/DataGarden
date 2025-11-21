package com.cryptoneedle.garden.common.key.source;

import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>description: 数据源-维度-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString
@Embeddable
@Schema(description = "数据源-维度-主键")
public class SourceDimensionKey implements Serializable {

    @Schema(description = "目录")
    private String catalog;

    @Schema(description = "数据库")
    private String database;

    @Schema(description = "表")
    private String table;

    @Schema(description = "维度类型")
    private SourceDimensionType dimensionType;

    @Schema(description = "字段")
    private String dimension;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SourceDimensionKey that)) {
            return false;
        }
        return Objects.equals(catalog, that.catalog) && Objects.equals(database, that.database) && Objects.equals(table, that.table) && dimensionType == that.dimensionType && Objects.equals(dimension, that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog, database, table, dimensionType, dimension);
    }

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