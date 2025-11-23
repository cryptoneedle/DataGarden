package com.cryptoneedle.garden.infrastructure.repository.dws;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据汇总层(DWS)-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DwsTableRepository extends BaseRepository<DwsTable, DorisTableKey> {

    @Query("""
             FROM DwsTable
            ORDER BY id.databaseName, id.tableName
            """)
    List<DwsTable> tables();

    @Query("""
             FROM DwsTable
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName
            """)
    List<DwsTable> tables(String databaseName);
}