package com.cryptoneedle.garden.common.key.doris;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: Doris外部数据库-数据库-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
public class ExternalDatabaseKey implements Serializable {

    private String catalog;
    private String database;

    public DorisCatalogKey dorisCatalogKey() {
        return DorisCatalogKey.builder().catalog(this.catalog).build();
    }
}