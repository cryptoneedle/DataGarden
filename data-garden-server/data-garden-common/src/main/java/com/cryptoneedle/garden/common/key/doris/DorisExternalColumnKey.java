package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>description: Doris外部数据库-字段-主键 </p>
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
@Schema(description = "Doris外部数据库-字段-主键")
public class DorisExternalColumnKey implements Serializable {

    @Schema(description = "目录")
    private String catalog;

    @Schema(description = "数据库")
    private String database;

    @Schema(description = "表")
    private String table;

    @Schema(description = "字段")
    private String column;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DorisExternalColumnKey that)) {
            return false;
        }
        return Objects.equals(catalog, that.catalog) && Objects.equals(database, that.database) && Objects.equals(table, that.table) && Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog, database, table, column);
    }

    public DorisCatalogKey dorisCatalogKey() {
        return DorisCatalogKey.builder().catalog(this.catalog).build();
    }

    public DorisExternalDatabaseKey dorisExternalDatabaseKey() {
        return DorisExternalDatabaseKey.builder().catalog(this.catalog).database(this.database).build();
    }

    public DorisExternalTableKey dorisExternalTableKey() {
        return DorisExternalTableKey.builder().catalog(this.catalog).database(this.database).table(this.table).build();
    }
}