package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>description: Doris外部数据库-数据库-主键 </p>
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
@Schema(description = "Doris外部数据库-数据库-主键")
public class DorisExternalDatabaseKey implements Serializable {

    @Schema(description = "目录")
    private String catalog;

    @Schema(description = "数据库")
    private String database;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DorisExternalDatabaseKey that)) {
            return false;
        }
        return Objects.equals(catalog, that.catalog) && Objects.equals(database, that.database);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog, database);
    }

    public DorisCatalogKey dorisCatalogKey() {
        return DorisCatalogKey.builder().catalog(this.catalog).build();
    }
}