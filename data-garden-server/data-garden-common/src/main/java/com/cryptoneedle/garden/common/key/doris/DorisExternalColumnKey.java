package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: Doris外部数据库-字段-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
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