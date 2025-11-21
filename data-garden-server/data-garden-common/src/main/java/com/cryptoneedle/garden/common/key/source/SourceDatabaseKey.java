package com.cryptoneedle.garden.common.key.source;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>description: 数据源-数据库-主键 </p>
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
@Schema(description = "数据源-数据库-主键")
public class SourceDatabaseKey implements Serializable {

    @Column(name = "\"catalog\"")
    @Schema(description = "目录")
    private String catalog;

    @Column(name = "\"database\"")
    @Schema(description = "数据库")
    private String database;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SourceDatabaseKey that)) {
            return false;
        }
        return Objects.equals(catalog, that.catalog) && Objects.equals(database, that.database);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog, database);
    }

    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalog(this.catalog).build();
    }
}