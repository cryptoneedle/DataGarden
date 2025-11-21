package com.cryptoneedle.garden.infrastructure.repository.doris;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: DORIS-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DorisTableRepository extends BaseRepository<DorisTable, DorisTableKey> {

    @Query("""
             FROM DorisTable
            WHERE id.databaseName = :databaseName
              AND id.tableName = :tableName
            """)
    DorisTable database(String databaseName, String tableName);

    @Query("""
             FROM DorisTable
            ORDER BY databaseSort, id.databaseName, id.tableName
            """)
    List<DorisTable> databases();

    @Query("""
             FROM DorisTable
            WHERE id.databaseName = :databaseName
            ORDER BY databaseSort, id.databaseName, id.tableName
            """)
    List<DorisTable> databases(String databaseName);
}