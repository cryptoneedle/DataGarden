package com.cryptoneedle.garden.common.key.doris;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: Doris外部数据库-表-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
public class ExternalTableKey implements Serializable {

    private String catalog;
    private String database;
    private String table;

    public DorisCatalogKey dorisCatalogKey() {
        return DorisCatalogKey.builder().catalog(this.catalog).build();
    }

    public ExternalDatabaseKey externalDatabaseKey() {
        return ExternalDatabaseKey.builder().catalog(this.catalog).database(this.database).build();
    }
}