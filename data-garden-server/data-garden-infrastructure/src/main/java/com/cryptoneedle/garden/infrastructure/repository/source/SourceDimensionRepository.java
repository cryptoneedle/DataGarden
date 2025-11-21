package com.cryptoneedle.garden.infrastructure.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.source.SourceDimensionKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDimension;
import org.springframework.stereotype.Repository;

/**
 * <p>description: 数据源-维度-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceDimensionRepository extends BaseRepository<SourceDimension, SourceDimensionKey> {
}