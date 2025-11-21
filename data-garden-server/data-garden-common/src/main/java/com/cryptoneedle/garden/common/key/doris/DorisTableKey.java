package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: Doris数据库-表-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
@Schema(description = "Doris数据库-表-主键")
public class DorisTableKey implements Serializable {

    @Column(name = "\"database\"", length = 64)
    @Schema(description = "数据库")
    private String database;

    @Column(name = "\"table\"", length = 64)
    @Schema(description = "表")
    private String table;

    public DorisDatabaseKey dorisDatabaseKey() {
        return DorisDatabaseKey.builder().database(this.database).build();
    }
}