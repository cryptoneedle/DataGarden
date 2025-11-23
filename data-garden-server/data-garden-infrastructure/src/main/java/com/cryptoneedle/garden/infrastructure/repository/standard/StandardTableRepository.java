package com.cryptoneedle.garden.infrastructure.repository.standard;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据标准层(STANDARD)-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface StandardTableRepository extends BaseRepository<StandardTable, DorisTableKey> {

    @Query("""
             FROM StandardTable
            ORDER BY id.databaseName, id.tableName
            """)
    List<StandardTable> tables();

    @Query("""
             FROM StandardTable
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName
            """)
    List<StandardTable> tables(String databaseName);
}