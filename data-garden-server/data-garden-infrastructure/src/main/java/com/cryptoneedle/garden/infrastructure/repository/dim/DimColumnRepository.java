package com.cryptoneedle.garden.infrastructure.repository.dim;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.dim.DimColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据维度层(DIM)-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DimColumnRepository extends BaseRepository<DimColumn, DorisColumnKey> {

    @Query("""
             FROM DimColumn
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<DimColumn> columns();

    @Query("""
             FROM DimColumn
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<DimColumn> columns(String tableName);
}