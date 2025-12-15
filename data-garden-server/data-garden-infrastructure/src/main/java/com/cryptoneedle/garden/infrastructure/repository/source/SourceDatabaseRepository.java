package com.cryptoneedle.garden.infrastructure.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDatabase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据源-数据库-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceDatabaseRepository extends BaseRepository<SourceDatabase, SourceDatabaseKey> {
    
    @Query("""
             FROM SourceDatabase
            ORDER BY id.catalogName, enabled DESC, id.databaseName
            """)
    List<SourceDatabase> databases();
    
    @Query("""
             FROM SourceDatabase
            WHERE id.catalogName = :catalogName
            ORDER BY id.catalogName, enabled DESC, id.databaseName
            """)
    List<SourceDatabase> databases(String catalogName);
    
    @Query("""
             FROM SourceDatabase
            WHERE enabled = TRUE
            ORDER BY id.catalogName, enabled DESC, id.databaseName
            """)
    List<SourceDatabase> databasesEnabled();
    
    @Query("""
             FROM SourceDatabase
            WHERE id.catalogName = :catalogName
              AND enabled = TRUE
            ORDER BY id.catalogName, enabled DESC, id.databaseName
            """)
    List<SourceDatabase> databasesEnabled(String catalogName);
}