package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDatabase;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceTable;
import com.cryptoneedle.garden.infrastructure.repository.source.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 部分更新数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchSourceService {
    
    private final SelectConfigService selectConfigService;
    private final SelectSourceService select;
    private final SaveSourceService save;
    private final SourceCatalogRepository sourceCatalogRepository;
    private final SourceDatabaseRepository sourceDatabaseRepository;
    private final SourceTableRepository sourceTableRepository;
    private final SourceColumnRepository sourceColumnRepository;
    private final SourceDimensionRepository sourceDimensionRepository;
    
    public PatchSourceService(SelectConfigService selectConfigService,
                              SelectSourceService selectSourceService,
                              SaveSourceService saveSourceService,
                              SourceCatalogRepository sourceCatalogRepository,
                              SourceDatabaseRepository sourceDatabaseRepository,
                              SourceTableRepository sourceTableRepository,
                              SourceColumnRepository sourceColumnRepository,
                              SourceDimensionRepository sourceDimensionRepository) {
        this.selectConfigService = selectConfigService;
        this.select = selectSourceService;
        this.save = saveSourceService;
        this.sourceCatalogRepository = sourceCatalogRepository;
        this.sourceDatabaseRepository = sourceDatabaseRepository;
        this.sourceTableRepository = sourceTableRepository;
        this.sourceColumnRepository = sourceColumnRepository;
        this.sourceDimensionRepository = sourceDimensionRepository;
    }
    
    /**
     * SourceCatalog
     */
    
    /**
     * SourceDatabase
     */
    public void databaseDefault(SourceCatalog catalog) {
        List<SourceDatabase> databases = select.databases();
        for (SourceDatabase database : databases) {
            if (database.getDorisCatalog() == null
                    || database.getSystemCode() == null
                    || database.getCollectFrequency() == null
                    || database.getCollectTimePoint() == null) {
                if (database.getDorisCatalog() == null) {
                    database.setDorisCatalog(catalog.getDorisCatalog());
                }
                if (database.getSystemCode() == null) {
                    database.setSystemCode(catalog.getSystemCode());
                }
                if (database.getCollectFrequency() == null) {
                    database.setCollectFrequency(catalog.getCollectFrequency());
                }
                if (database.getCollectTimePoint() == null) {
                    database.setCollectTimePoint(catalog.getCollectTimePoint());
                }
                save.database(database);
            }
            
        }
    }
    
    /**
     * SourceTable
     */
    public void tableDefault(SourceCatalog catalog) {
        List<SourceTable> tables = select.tables();
        for (SourceTable table : tables) {
            if (table.getDorisCatalog() == null
                    || table.getSystemCode() == null
                    || table.getCollectFrequency() == null
                    || table.getCollectTimePoint() == null) {
                if (table.getDorisCatalog() == null) {
                    table.setDorisCatalog(catalog.getDorisCatalog());
                }
                if (table.getSystemCode() == null) {
                    table.setSystemCode(catalog.getSystemCode());
                }
                if (table.getCollectFrequency() == null) {
                    table.setCollectFrequency(catalog.getCollectFrequency());
                }
                if (table.getCollectTimePoint() == null) {
                    table.setCollectTimePoint(catalog.getCollectTimePoint());
                }
                save.table(table);
            }
            
        }
    }
    
    /**
     * SourceColumn
     */
    
    /**
     * SourceDimension
     */
}