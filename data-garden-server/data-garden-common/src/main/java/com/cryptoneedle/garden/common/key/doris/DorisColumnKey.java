package com.cryptoneedle.garden.common.key.doris;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>description: Doris数据库-字段-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@Builder
@Accessors(chain = true)
public class DorisColumnKey implements Serializable {

    private String database;
    private String table;
    private String column;

    public DorisDatabaseKey dorisDatabaseKey() {
        return DorisDatabaseKey.builder().database(this.database).build();
    }

    public DorisTableKey dorisTableKey() {
        return DorisTableKey.builder().database(this.database).table(this.table).build();
    }
}