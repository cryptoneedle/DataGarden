package com.cryptoneedle.garden.infrastructure.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据源-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceTableRepository extends BaseRepository<SourceTable, SourceTableKey> {
    
    @Query("""
             FROM SourceTable
            ORDER BY id.catalogName, id.databaseName, enabled DESC, id.tableName
            """)
    List<SourceTable> tables();
    
    @Query("""
             FROM SourceTable
            WHERE id.catalogName = :catalogName
            ORDER BY id.catalogName, id.databaseName, enabled DESC, id.tableName
            """)
    List<SourceTable> tables(String catalogName);
    
    @Query("""
             FROM SourceTable
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
            ORDER BY id.catalogName, id.databaseName, enabled DESC, id.tableName
            """)
    List<SourceTable> tables(String catalogName, String databaseName);
    
    @Query("""
             FROM SourceTable
            WHERE enabled = TRUE
            ORDER BY id.catalogName, id.databaseName, enabled DESC, id.tableName
            """)
    List<SourceTable> tablesEnabled();
    
    @Query("""
             FROM SourceTable
            WHERE id.catalogName = :catalogName
              AND enabled = TRUE
            ORDER BY id.catalogName, id.databaseName, enabled DESC, id.tableName
            """)
    List<SourceTable> tablesEnabled(String catalogName);
    
    @Query("""
             FROM SourceTable
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND enabled = TRUE
            ORDER BY id.catalogName, id.databaseName, enabled DESC, id.tableName
            """)
    List<SourceTable> tablesEnabled(String catalogName, String databaseName);
    
    @Query("""
             FROM SourceTable
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND enabled = TRUE
              AND collectFrequency = :collectFrequency
              AND collectTimePoint = :collectTimePoint
              AND collectGroupNum = :collectGroupNum
            ORDER BY id.catalogName, id.databaseName, enabled DESC, id.tableName
            """)
    List<SourceTable> tablesByCollect(String catalogName, String databaseName, SourceCollectFrequencyType collectFrequency, Integer collectTimePoint, Integer collectGroupNum);
    
    @Query("""
             FROM SourceTable t
            WHERE t.id.catalogName = :catalogName
              AND t.id.databaseName = :databaseName
              AND t.enabled = TRUE
              AND EXISTS (SELECT 1
                            FROM SourceColumn c
                           WHERE t.id.catalogName = c.id.catalogName
                             AND t.id.databaseName = c.id.databaseName
                             AND t.id.tableName = c.id.tableName
                             AND c.incremented = TRUE)
            ORDER BY t.id.catalogName, t.id.databaseName, t.enabled DESC, t.id.tableName
            """)
    List<SourceTable> tablesByFullCollect(String catalogName, String databaseName);
}