package com.cryptoneedle.garden.infrastructure.repository.doris;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: DORIS-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DorisColumnRepository extends BaseRepository<DorisColumn, DorisColumnKey> {

    @Query("""
             FROM DorisColumn
            ORDER BY databaseSort, id.databaseName, id.tableName, sort
            """)
    List<DorisColumn> columns();

    @Query("""
             FROM DorisColumn
            WHERE id.databaseName = :databaseName
            ORDER BY databaseSort, id.databaseName, id.tableName, sort
            """)
    List<DorisColumn> columns(String databaseName);

    @Query("""
             FROM DorisColumn
            WHERE id.databaseName = :databaseName
              AND id.tableName = :tableName
            ORDER BY databaseSort, id.databaseName, id.tableName, sort
            """)
    List<DorisColumn> columns(String databaseName, String tableName);
}