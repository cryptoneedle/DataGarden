package com.cryptoneedle.garden.infrastructure.repository.dim;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.dim.DimTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据维度层(DIM)-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DimTableRepository extends BaseRepository<DimTable, DorisTableKey> {

    @Query("""
             FROM DimTable
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName
            """)
    DimTable table(String tableName);

    @Query("""
             FROM DimTable
            ORDER BY id.databaseName, id.tableName
            """)
    List<DimTable> tables();

    @Query("""
             FROM DimTable
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName
            """)
    List<DimTable> tables(String databaseName);
}