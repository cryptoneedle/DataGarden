package com.cryptoneedle.garden.infrastructure.repository.source;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 数据源-目录-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface SourceCatalogRepository extends BaseRepository<SourceCatalog, SourceCatalogKey> {

    @Query("""
             FROM SourceCatalog
            WHERE id.catalogName = :catalogName
            """)
    SourceCatalog catalog(String catalogName);

    @Query("""
             FROM SourceCatalog
            WHERE dorisCatalogName = :dorisCatalogName
            """)
    SourceCatalog catalogByDoris(String dorisCatalogName);

    @Query("""
             FROM SourceCatalog
            ORDER BY enabled DESC, id.catalogName
            """)
    List<SourceCatalog> catalogs();

    @Query("""
             FROM SourceCatalog
            WHERE enabled = TRUE
            ORDER BY enabled DESC, id.catalogName
            """)
    List<SourceCatalog> catalogsEnabled();

    @Query("""
             FROM SourceCatalog
            WHERE host = :host
              AND port = :port
            ORDER BY enabled DESC, id.catalogName
            """)
    List<SourceCatalog> catalogsByServer(String host, Integer port);
}