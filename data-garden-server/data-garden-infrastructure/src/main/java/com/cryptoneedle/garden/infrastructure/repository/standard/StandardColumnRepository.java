package com.cryptoneedle.garden.infrastructure.repository.standard;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据标准层(STANDARD)-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface StandardColumnRepository extends BaseRepository<StandardColumn, DorisColumnKey> {
    
    @Query("""
             FROM StandardColumn
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<StandardColumn> columns();
    
    @Query("""
             FROM StandardColumn
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<StandardColumn> columns(String tableName);
}