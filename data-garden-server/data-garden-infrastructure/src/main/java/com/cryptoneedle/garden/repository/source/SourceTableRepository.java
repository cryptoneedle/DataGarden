package com.cryptoneedle.garden.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.entity.source.SourceTable;
import org.springframework.stereotype.Repository;

/**
 * <p>description: 数据源-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceTableRepository extends BaseRepository<SourceTable, SourceTableKey> {
}