package com.cryptoneedle.garden.infrastructure.repository.mapping;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据映射层(MAPPING)-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2026-02-03
 */
@Repository
public interface MappingTableRepository extends BaseRepository<MappingTable, DorisTableKey> {
    
    @Query("""
             FROM MappingTable
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName
            """)
    MappingTable table(String tableName);
    
    @Query("""
             FROM MappingTable
            ORDER BY id.databaseName, id.tableName
            """)
    List<MappingTable> tables();
    
    @Query("""
             FROM MappingTable
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName
            """)
    List<MappingTable> tables(String databaseName);
}