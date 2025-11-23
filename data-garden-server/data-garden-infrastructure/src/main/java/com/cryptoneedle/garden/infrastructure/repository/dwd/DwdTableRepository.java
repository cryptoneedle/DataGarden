package com.cryptoneedle.garden.infrastructure.repository.dwd;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据明细层(DWD)-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DwdTableRepository extends BaseRepository<DwdTable, DorisTableKey> {

    @Query("""
             FROM DwdTable
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName
            """)
    DwdTable table(String tableName);

    @Query("""
             FROM DwdTable
            ORDER BY id.databaseName, id.tableName
            """)
    List<DwdTable> tables();

    @Query("""
             FROM DwdTable
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName
            """)
    List<DwdTable> tables(String databaseName);
}