package com.cryptoneedle.garden.infrastructure.repository.dwd;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据明细层(DWD)-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DwdColumnRepository extends BaseRepository<DwdColumn, DorisColumnKey> {

    @Query("""
             FROM DwdColumn
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<DwdColumn> columns();

    @Query("""
             FROM DwdColumn
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<DwdColumn> columns(String databaseName);

    @Query("""
             FROM DwdColumn
            WHERE id.databaseName = :databaseName
              AND id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<DwdColumn> columns(String databaseName, String tableName);
}