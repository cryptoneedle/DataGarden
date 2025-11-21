package com.cryptoneedle.garden.infrastructure.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceColumn;
import org.springframework.stereotype.Repository;

/**
 * <p>description: 数据源-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceColumnRepository extends BaseRepository<SourceColumn, SourceColumnKey> {
}