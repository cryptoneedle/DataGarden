package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>description: Doris数据库-目录-主键 </p>
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
@Schema(description = "Doris数据库-目录-主键")
public class DorisCatalogKey implements Serializable {

    @Column(name = "\"catalog\"", length = 512)
    @Schema(description = "目录")
    private String catalog;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DorisCatalogKey that)) {
            return false;
        }
        return Objects.equals(catalog, that.catalog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalog);
    }
}