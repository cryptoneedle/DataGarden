package com.cryptoneedle.garden.core.crud.doris;

import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisCatalogKey;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisDatabaseKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisCatalog;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisDatabase;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisCatalogRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisDatabaseRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 查询Doris数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SelectDorisService {
    
    private final DorisCatalogRepository dorisCatalogRepository;
    private final DorisDatabaseRepository dorisDatabaseRepository;
    private final DorisTableRepository dorisTableRepository;
    private final DorisColumnRepository dorisColumnRepository;
    
    public SelectDorisService(DorisCatalogRepository dorisCatalogRepository,
                              DorisDatabaseRepository dorisDatabaseRepository,
                              DorisTableRepository dorisTableRepository,
                              DorisColumnRepository dorisColumnRepository) {
        this.dorisCatalogRepository = dorisCatalogRepository;
        this.dorisDatabaseRepository = dorisDatabaseRepository;
        this.dorisTableRepository = dorisTableRepository;
        this.dorisColumnRepository = dorisColumnRepository;
    }
    
    /**
     * DorisCatalog
     */
    public DorisCatalog catalog(DorisCatalogKey id) {
        return dorisCatalogRepository.findById(id).orElse(null);
    }
    
    public DorisCatalog catalog(String catalogName) {
        return catalog(DorisCatalogKey.builder().catalogName(catalogName).build());
    }
    
    public DorisCatalog catalogCheck(DorisCatalogKey id) throws EntityNotFoundException {
        return dorisCatalogRepository.findById(id)
                                     .orElseThrow(() -> new EntityNotFoundException("DorisCatalog", id.toString()));
    }
    
    public DorisCatalog catalogBySource(String sourceCatalogName) {
        return dorisCatalogRepository.catalogBySource(sourceCatalogName);
    }
    
    public List<DorisCatalog> catalogs() {
        return dorisCatalogRepository.catalogs();
    }
    
    /**
     * DorisDatabase
     */
    public DorisDatabase database(DorisDatabaseKey id) {
        return dorisDatabaseRepository.findById(id).orElse(null);
    }
    
    public DorisDatabase database(String databaseName) {
        return database(DorisDatabaseKey.builder().databaseName(databaseName).build());
    }
    
    public DorisDatabase databaseCheck(DorisDatabaseKey id) throws EntityNotFoundException {
        return dorisDatabaseRepository.findById(id)
                                      .orElseThrow(() -> new EntityNotFoundException("DorisDatabase", id.toString()));
    }
    
    public List<DorisDatabase> databases() {
        return dorisDatabaseRepository.databases();
    }
    
    /**
     * DorisTable
     */
    public DorisTable table(DorisTableKey id) {
        return dorisTableRepository.findById(id).orElse(null);
    }
    
    public DorisTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return dorisTableRepository.findById(id)
                                   .orElseThrow(() -> new EntityNotFoundException("DorisTable", id.toString()));
    }
    
    public List<DorisTable> tables() {
        return dorisTableRepository.tables();
    }
    
    public List<DorisTable> tables(String databaseName) {
        return dorisTableRepository.tables(databaseName);
    }
    
    /**
     * DorisColumn
     */
    public DorisColumn column(DorisColumnKey id) {
        return dorisColumnRepository.findById(id).orElse(null);
    }
    
    public DorisColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return dorisColumnRepository.findById(id)
                                    .orElseThrow(() -> new EntityNotFoundException("DorisColumn", id.toString()));
    }
    
    public List<DorisColumn> columns() {
        return dorisColumnRepository.columns();
    }
    
    public List<DorisColumn> columns(String databaseName) {
        return dorisColumnRepository.columns(databaseName);
    }
    
    public List<DorisColumn> columns(String databaseName, String tableName) {
        return dorisColumnRepository.columns(databaseName, tableName);
    }
}