package com.cryptoneedle.garden.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.entity.source.SourceCatalog;
import org.springframework.stereotype.Repository;

/**
 * <p>description: 数据源-目录-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceCatalogRepository extends BaseRepository<SourceCatalog, SourceCatalogKey> {
}