package com.cryptoneedle.garden.common.key.source;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>description: 数据源-字段-主键 </p>
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
@Schema(description = "数据源-字段-主键")
public class SourceColumnKey implements Serializable {

    @Column(name = "\"catalog\"")
    @Schema(description = "目录")
    private String catalog;

    @Column(name = "\"database\"")
    @Schema(description = "数据库")
    private String database;

    @Column(name = "\"table\"")
    @Schema(description = "表")
    private String table;

    @Column(name = "\"column\"")
    @Schema(description = "字段")
    private String column;

    @Override
    public int hashCode() {
        return Objects.hash(catalog, database, table, column);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SourceColumnKey that)) {
            return false;
        }
        return Objects.equals(catalog, that.catalog) && Objects.equals(database, that.database) && Objects.equals(table, that.table) && Objects.equals(column, that.column);
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