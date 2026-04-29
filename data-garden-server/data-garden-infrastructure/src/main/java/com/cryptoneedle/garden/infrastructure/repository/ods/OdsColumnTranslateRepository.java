package com.cryptoneedle.garden.infrastructure.repository.ods;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.OdsColumnTranslateKey;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumnTranslate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 操作数据存储层(ODS)-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface OdsColumnTranslateRepository extends BaseRepository<OdsColumnTranslate, OdsColumnTranslateKey> {
    
    @Query("""
         FROM OdsColumnTranslate
        WHERE id.tableName = :tableName
          AND id.columnName = :columnName
        ORDER BY id.value
        """)
    List<OdsColumnTranslate> listByColumn(String tableName, String columnName);
    
    @Modifying
    @Query("""
        DELETE FROM OdsColumnTranslate
         WHERE id.tableName = :tableName
           AND id.columnName = :columnName
        """)
    void deleteByColumn(String tableName, String columnName);
}