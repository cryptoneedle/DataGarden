package com.cryptoneedle.garden.repository.doris;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisCatalogKey;
import com.cryptoneedle.garden.entity.doris.DorisCatalog;
import org.springframework.stereotype.Repository;

/**
 * <p>description: DORIS-目录-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DorisCatalogRepository extends BaseRepository<DorisCatalog, DorisCatalogKey> {
}