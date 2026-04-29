package com.cryptoneedle.garden.infrastructure.repository.mapping;


import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.MappingColumnRelyKey;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumnRely;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTableRely;
import org.springframework.data.jpa.repository.Modifying;
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
public interface MappingColumnRelyRepository extends BaseRepository<MappingColumnRely, MappingColumnRelyKey> {
    
    @Query("""
             FROM MappingColumnRely
            WHERE id.sourceDatabaseName = :databaseName
              AND id.sourceTableName = :tableName
              AND id.sourceColumnName = :columnName
            """)
    MappingColumnRely getBySource(String databaseName, String tableName, String columnName);
    
    @Modifying
    @Query("""
            DELETE
              FROM MappingColumnRely
             WHERE id.mappingDatabaseName = :mappingDatabaseName
               AND id.mappingTableName = :mappingTableName
               AND id.sourceDatabaseName = :sourceDatabaseName
               AND id.sourceTableName = :sourceTableName
           """)
    void deleteByMappingTableRely(String mappingDatabaseName,
                                  String mappingTableName,
                                  String sourceDatabaseName,
                                  String sourceTableName);
    
    @Query("""
              FROM MappingColumnRely
             WHERE id.mappingTableName = :mappingTableName
               AND id.sourceTableName = :sourceTableName
           """)
    List<MappingColumnRely> listColumnRelysByMapping(String mappingTableName, String sourceTableName);
}