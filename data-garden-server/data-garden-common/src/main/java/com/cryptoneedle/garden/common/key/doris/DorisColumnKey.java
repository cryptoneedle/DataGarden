package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>description: Doris数据库-字段-主键 </p>
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
@Schema(description = "Doris数据库-字段-主键")
public class DorisColumnKey implements Serializable {

    @Column(name = "\"database\"", length = 64)
    @Schema(description = "数据库")
    private String database;

    @Column(name = "\"table\"", length = 64)
    @Schema(description = "表")
    private String table;

    @Column(name = "\"column\"", length = 64)
    @Schema(description = "字段")
    private String column;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DorisColumnKey that)) {
            return false;
        }
        return Objects.equals(database, that.database) && Objects.equals(table, that.table) && Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(database, table, column);
    }

    public DorisDatabaseKey dorisDatabaseKey() {
        return DorisDatabaseKey.builder().database(this.database).build();
    }

    public DorisTableKey dorisTableKey() {
        return DorisTableKey.builder().database(this.database).table(this.table).build();
    }
}