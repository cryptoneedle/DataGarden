package com.cryptoneedle.garden.core.crud.doris;

import com.cryptoneedle.garden.common.key.doris.DorisCatalogKey;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisDatabaseKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisCatalog;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisDatabase;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.repository.doris.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 删除Doris数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DeleteDorisService {
    
    private final DorisCatalogRepository dorisCatalogRepository;
    private final DorisDatabaseRepository dorisDatabaseRepository;
    private final DorisTableRepository dorisTableRepository;
    private final DorisColumnRepository dorisColumnRepository;
    private final DorisTableRowNumRepository dorisTableRowNumRepository;
    
    public DeleteDorisService(DorisCatalogRepository dorisCatalogRepository,
                              DorisDatabaseRepository dorisDatabaseRepository,
                              DorisTableRepository dorisTableRepository,
                              DorisColumnRepository dorisColumnRepository,
                              DorisTableRowNumRepository dorisTableRowNumRepository) {
        this.dorisCatalogRepository = dorisCatalogRepository;
        this.dorisDatabaseRepository = dorisDatabaseRepository;
        this.dorisTableRepository = dorisTableRepository;
        this.dorisColumnRepository = dorisColumnRepository;
        this.dorisTableRowNumRepository = dorisTableRowNumRepository;
    }
    
    /**
     * DorisCatalog
     */
    public void catalog(DorisCatalogKey key) {
        dorisCatalogRepository.deleteById(key);
    }
    
    public void catalog(DorisCatalog entity) {
        dorisCatalogRepository.delete(entity);
    }
    
    public void catalogs(List<DorisCatalog> list) {
        dorisCatalogRepository.deleteAll(list);
    }
    
    /**
     * DorisDatabase
     */
    public void database(DorisDatabaseKey key) {
        dorisDatabaseRepository.deleteById(key);
    }
    
    public void database(DorisDatabase entity) {
        dorisDatabaseRepository.delete(entity);
    }
    
    public void databases(List<DorisDatabase> list) {
        dorisDatabaseRepository.deleteAll(list);
    }
    
    /**
     * DorisTable
     */
    public void table(DorisTableKey key) {
        dorisTableRepository.deleteById(key);
    }
    
    public void table(DorisTable entity) {
        dorisTableRepository.delete(entity);
    }
    
    public void tables(List<DorisTable> list) {
        dorisTableRepository.deleteAll(list);
    }
    
    /**
     * DorisColumn
     */
    public void column(DorisColumnKey key) {
        dorisColumnRepository.deleteById(key);
    }
    
    public void column(DorisColumn entity) {
        dorisColumnRepository.delete(entity);
    }
    
    public void columns(List<DorisColumn> list) {
        dorisColumnRepository.deleteAll(list);
    }
    
    /**
     * DorisTableRowNum
     */
}