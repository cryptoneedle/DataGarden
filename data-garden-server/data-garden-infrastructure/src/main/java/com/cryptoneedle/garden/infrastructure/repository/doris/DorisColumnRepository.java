package com.cryptoneedle.garden.infrastructure.repository.doris;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import org.springframework.stereotype.Repository;

/**
 * <p>description: DORIS-字段-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DorisColumnRepository extends BaseRepository<DorisColumn, DorisColumnKey> {
}