package com.cryptoneedle.garden.infrastructure.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据源-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceColumnRepository extends BaseRepository<SourceColumn, SourceColumnKey> {
    
    @Query("""
             FROM SourceColumn
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, sort
            """)
    List<SourceColumn> columns();
    
    @Query("""
             FROM SourceColumn
            WHERE id.catalogName = :catalogName
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, sort
            """)
    List<SourceColumn> columns(String catalogName);
    
    @Query("""
             FROM SourceColumn
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, sort
            """)
    List<SourceColumn> columns(String catalogName, String databaseName);
    
    @Query("""
             FROM SourceColumn
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND id.tableName = :tableName
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, sort
            """)
    List<SourceColumn> columns(String catalogName, String databaseName, String tableName);
    
    @Query("""
              FROM SourceColumn
             WHERE enabled = TRUE
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, sort
            """)
    List<SourceColumn> columnsEnabled();
    
    @Query("""
             FROM SourceColumn
            WHERE id.catalogName = :catalogName
              AND enabled = TRUE
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, sort
            """)
    List<SourceColumn> columnsEnabled(String catalogName);
    
    @Query("""
             FROM SourceColumn
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND enabled = TRUE
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, sort
            """)
    List<SourceColumn> columnsEnabled(String catalogName, String databaseName);
    
    @Query("""
             FROM SourceColumn
            WHERE id.catalogName = :catalogName
              AND id.databaseName = :databaseName
              AND id.tableName = :tableName
              AND enabled = TRUE
            ORDER BY id.catalogName, id.databaseName, id.tableName, enabled DESC, sort
            """)
    List<SourceColumn> columnsEnabled(String catalogName, String databaseName, String tableName);
    
    @Query("""
             FROM SourceColumn c
            WHERE c.id.catalogName = :catalogName
              AND c.id.databaseName = :databaseName
              AND c.id.tableName = :tableName
              AND EXISTS (SELECT 1
                            FROM SourceDimension d
                           WHERE c.id.catalogName = d.id.catalogName
                             AND c.id.databaseName = d.id.databaseName
                             AND c.id.tableName = d.id.tableName
                             AND c.id.columnName = d.id.columnName
                             AND d.enabled = TRUE)
            ORDER BY c.id.catalogName, c.id.databaseName, c.id.tableName, c.sort, c.id.columnName""")
    List<SourceColumn> columnsWithDimension(String catalogName, String databaseName, String tableName);
    
    @Query("""
             FROM SourceColumn c
            WHERE c.id.catalogName = :catalogName
              AND c.id.databaseName = :databaseName
              AND c.id.tableName = :tableName
              AND NOT EXISTS (SELECT 1
                                FROM SourceDimension d
                               WHERE c.id.catalogName = d.id.catalogName
                                 AND c.id.databaseName = d.id.databaseName
                                 AND c.id.tableName = d.id.tableName
                                 AND c.id.columnName = d.id.columnName
                                 AND d.enabled = TRUE)
            ORDER BY c.id.catalogName, c.id.databaseName, c.id.tableName, c.sort, c.id.columnName""")
    List<SourceColumn> columnsWithoutDimension(String catalogName, String databaseName, String tableName);
}