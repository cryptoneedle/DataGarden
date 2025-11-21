package com.cryptoneedle.garden.infrastructure.repository.doris;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisDatabaseKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisDatabase;
import org.springframework.stereotype.Repository;

/**
 * <p>description: DORIS-数据库-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DorisDatabaseRepository extends BaseRepository<DorisDatabase, DorisDatabaseKey> {
}