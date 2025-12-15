package com.cryptoneedle.garden.infrastructure.repository.dws;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据汇总层(DWS)-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DwsColumnRepository extends BaseRepository<DwsColumn, DorisColumnKey> {
    
    @Query("""
             FROM DwsColumn
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<DwsColumn> columns();
    
    @Query("""
             FROM DwsColumn
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<DwsColumn> columns(String tableName);
}