package com.cryptoneedle.garden.infrastructure.repository.doris;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import org.springframework.stereotype.Repository;

/**
 * <p>description: DORIS-表-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DorisTableRepository extends BaseRepository<DorisTable, DorisTableKey> {
}