package com.cryptoneedle.garden.infrastructure.repository.ads;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsColumn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据应用层(ADS)-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface AdsColumnRepository extends BaseRepository<AdsColumn, DorisColumnKey> {

    @Query("""
             FROM AdsColumn
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<AdsColumn> columns();

    @Query("""
             FROM AdsColumn
            WHERE id.tableName = :tableName
            ORDER BY id.databaseName, id.tableName, sort
            """)
    List<AdsColumn> columns(String tableName);
}