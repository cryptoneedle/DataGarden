package com.cryptoneedle.garden.infrastructure.repository.mapping;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.MappingTableRelyKey;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTableRely;
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
public interface MappingTableRelyRepository extends BaseRepository<MappingTableRely, MappingTableRelyKey> {

    @Query("""
             FROM MappingTableRely
            WHERE id.sourceDatabaseName = :databaseName
              AND id.sourceTableName = :tableName
            ORDER BY id.mappingDatabaseName, id.mappingTableName
            """)
    List<MappingTableRely> findBySource(String databaseName, String tableName);
}