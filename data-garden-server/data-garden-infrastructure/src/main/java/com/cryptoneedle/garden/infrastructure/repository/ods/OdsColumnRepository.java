package com.cryptoneedle.garden.infrastructure.repository.ods;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 操作数据存储层(ODS)-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface OdsColumnRepository extends BaseRepository<OdsColumn, DorisColumnKey> {

    @Query("""
             FROM OdsColumn
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<OdsColumn> columns();

    @Query("""
             FROM OdsColumn
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<OdsColumn> columns(String databaseName);

    @Query("""
             FROM OdsColumn
            WHERE id.databaseName = :databaseName
              AND id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<OdsColumn> columns(String databaseName, String tableName);
}