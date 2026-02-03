package com.cryptoneedle.garden.infrastructure.repository.mapping;


import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据映射层(MAPPING)-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2026-02-03
 */
@Repository
public interface MappingColumnRepository  extends BaseRepository<MappingColumn, DorisColumnKey> {
    
    @Query("""
             FROM MappingColumn
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<MappingColumn> columns();
    
    @Query("""
             FROM MappingColumn
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<MappingColumn> columns(String tableName);
}