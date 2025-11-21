package com.cryptoneedle.garden.infrastructure.repository.doris;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisCatalogKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisCatalog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: DORIS-目录-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DorisCatalogRepository extends BaseRepository<DorisCatalog, DorisCatalogKey> {

    @Query("""
             FROM DorisCatalog
            WHERE id.catalogName = :catalogName
            """)
    DorisCatalog catalog(String catalogName);

    @Query("""
             FROM DorisCatalog
            WHERE sourceCatalogName = :sourceCatalogName
            """)
    DorisCatalog catalogBySource(String sourceCatalogName);

    @Query("""
             FROM DorisCatalog
            ORDER BY CASE id.catalogName WHEN 'internal' THEN 0 ELSE 1 END, updateDt DESC
            """)
    List<DorisCatalog> catalogs();
}