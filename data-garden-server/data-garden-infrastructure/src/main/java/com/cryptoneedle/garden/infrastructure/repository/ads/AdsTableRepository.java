package com.cryptoneedle.garden.infrastructure.repository.ads;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据应用层(ADS)-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface AdsTableRepository extends BaseRepository<AdsTable, DorisTableKey> {

    @Query("""
             FROM AdsTable
            ORDER BY id.databaseName, id.tableName
            """)
    List<AdsTable> tables();

    @Query("""
             FROM AdsTable
            WHERE id.databaseName = :databaseName
            ORDER BY id.databaseName, id.tableName
            """)
    List<AdsTable> tables(String databaseName);
}