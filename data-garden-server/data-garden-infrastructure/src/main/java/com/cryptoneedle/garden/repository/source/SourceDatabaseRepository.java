package com.cryptoneedle.garden.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.entity.source.SourceDatabase;
import org.springframework.stereotype.Repository;

/**
 * <p>description: 数据源-数据库-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceDatabaseRepository extends BaseRepository<SourceDatabase, SourceDatabaseKey> {
}