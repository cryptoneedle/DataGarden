package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.infrastructure.repository.source.*;
import org.springframework.stereotype.Service;

/**
 * <p>description: 查询数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class SelectSourceService {

    private final SourceCatalogRepository sourceCatalogRepository;
    private final SourceDatabaseRepository sourceDatabaseRepository;
    private final SourceTableRepository sourceTableRepository;
    private final SourceColumnRepository sourceColumnRepository;
    private final SourceDimensionRepository sourceDimensionRepository;

    public SelectSourceService(SourceCatalogRepository sourceCatalogRepository,
                            SourceDatabaseRepository sourceDatabaseRepository,
                            SourceTableRepository sourceTableRepository,
                            SourceColumnRepository sourceColumnRepository,
                            SourceDimensionRepository sourceDimensionRepository) {
        this.sourceCatalogRepository = sourceCatalogRepository;
        this.sourceDatabaseRepository = sourceDatabaseRepository;
        this.sourceTableRepository = sourceTableRepository;
        this.sourceColumnRepository = sourceColumnRepository;
        this.sourceDimensionRepository = sourceDimensionRepository;
    }
}