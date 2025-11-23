package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.repository.source.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description: 新增数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class AddSourceService {

    private final SaveSourceService saveSourceService;
    private final SourceCatalogRepository sourceCatalogRepository;
    private final SourceDatabaseRepository sourceDatabaseRepository;
    private final SourceTableRepository sourceTableRepository;
    private final SourceColumnRepository sourceColumnRepository;
    private final SourceDimensionRepository sourceDimensionRepository;

    public AddSourceService(SaveSourceService saveSourceService,
                            SourceCatalogRepository sourceCatalogRepository,
                            SourceDatabaseRepository sourceDatabaseRepository,
                            SourceTableRepository sourceTableRepository,
                            SourceColumnRepository sourceColumnRepository,
                            SourceDimensionRepository sourceDimensionRepository) {
        this.saveSourceService = saveSourceService;
        this.sourceCatalogRepository = sourceCatalogRepository;
        this.sourceDatabaseRepository = sourceDatabaseRepository;
        this.sourceTableRepository = sourceTableRepository;
        this.sourceColumnRepository = sourceColumnRepository;
        this.sourceDimensionRepository = sourceDimensionRepository;
    }

    /**
     * SourceCatalog
     */
    public void catalog(SourceCatalog entity) {
        saveSourceService.catalog(entity);
    }

    public void catalogs(List<SourceCatalog> list) {
        saveSourceService.catalogs(list);
    }

    /**
     * SourceDatabase
     */
    public void database(SourceDatabase entity) {
        saveSourceService.database(entity);
    }

    public void databases(List<SourceDatabase> list) {
        saveSourceService.databases(list);
    }

    /**
     * SourceTable
     */
    public void table(SourceTable entity) {
        saveSourceService.table(entity);
    }

    public void tables(List<SourceTable> list) {
        saveSourceService.tables(list);
    }

    /**
     * SourceColumn
     */
    public void column(SourceColumn entity) {
        saveSourceService.column(entity);
    }

    public void columns(List<SourceColumn> list) {
        saveSourceService.columns(list);
    }

    /**
     * SourceDimension
     */
    public void dimension(SourceDimension entity) {
        saveSourceService.dimension(entity);
    }

    public void dimensions(List<SourceDimension> list) {
        saveSourceService.dimensions(list);
    }
}