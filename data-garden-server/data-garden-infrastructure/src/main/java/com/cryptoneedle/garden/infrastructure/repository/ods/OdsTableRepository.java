package com.cryptoneedle.garden.infrastructure.repository.ods;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 操作数据存储层(ODS)-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface OdsTableRepository extends BaseRepository<OdsTable, DorisTableKey> {
    
    @Query("""
             FROM OdsTable
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName
            """)
    OdsTable table(String tableName);
    
    @Query("""
             FROM OdsTable
            ORDER BY id.databaseName, id.tableName
            """)
    List<OdsTable> tables();
    
    @Query("""
             FROM OdsTable
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName
            """)
    List<OdsTable> tables(String databaseName);
}