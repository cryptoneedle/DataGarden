package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.common.key.source.*;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.repository.source.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 删除数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DeleteSourceService {
    
    private final SourceCatalogRepository sourceCatalogRepository;
    private final SourceDatabaseRepository sourceDatabaseRepository;
    private final SourceTableRepository sourceTableRepository;
    private final SourceColumnRepository sourceColumnRepository;
    private final SourceDimensionRepository sourceDimensionRepository;
    
    public DeleteSourceService(SourceCatalogRepository sourceCatalogRepository,
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
    
    /**
     * SourceCatalog
     */
    public void catalog(SourceCatalogKey key) {
        sourceCatalogRepository.deleteById(key);
    }
    
    public void catalog(SourceCatalog entity) {
        sourceCatalogRepository.delete(entity);
    }
    
    public void catalogs(List<SourceCatalog> entity) {
        sourceCatalogRepository.deleteAll(entity);
    }
    
    /**
     * SourceDatabase
     */
    public void database(SourceDatabaseKey key) {
        sourceDatabaseRepository.deleteById(key);
    }
    
    public void database(SourceDatabase entity) {
        sourceDatabaseRepository.delete(entity);
    }
    
    public void databases(List<SourceDatabase> entity) {
        sourceDatabaseRepository.deleteAll(entity);
    }
    
    /**
     * SourceTable
     */
    public void table(SourceTableKey key) {
        sourceTableRepository.deleteById(key);
    }
    
    public void table(SourceTable entity) {
        sourceTableRepository.delete(entity);
    }
    
    public void tables(List<SourceTable> entity) {
        sourceTableRepository.deleteAll(entity);
    }
    
    /**
     * SourceColumn
     */
    public void column(SourceColumnKey key) {
        sourceColumnRepository.deleteById(key);
    }
    
    public void column(SourceColumn entity) {
        sourceColumnRepository.delete(entity);
    }
    
    public void columns(List<SourceColumn> entity) {
        sourceColumnRepository.deleteAll(entity);
    }
    
    /**
     * SourceDimension
     */
    public void dimension(SourceDimensionKey key) {
        sourceDimensionRepository.deleteById(key);
    }
    
    public void dimension(SourceDimension entity) {
        sourceDimensionRepository.delete(entity);
    }
    
    public void dimensions(List<SourceDimension> entity) {
        sourceDimensionRepository.deleteAll(entity);
    }
}