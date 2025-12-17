package com.cryptoneedle.garden.core.crud.doris;

import com.cryptoneedle.garden.infrastructure.entity.doris.DorisCatalog;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisDatabase;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 新增Doris数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class AddDorisService {
    
    private final SelectDorisService select;
    private final SaveDorisService save;
    
    public AddDorisService(SelectDorisService selectDorisService,
                         SaveDorisService saveDorisService) {
        this.select = selectDorisService;
        this.save = saveDorisService;
    }
    
    /**
     * DorisCatalog
     */
    public void catalog(DorisCatalog entity) {
        save.catalog(entity);
    }
    
    public void catalogs(List<DorisCatalog> list) {
        save.catalogs(list);
    }
    
    /**
     * DorisDatabase
     */
    public void database(DorisDatabase entity) {
        save.database(entity);
    }
    
    public void databases(List<DorisDatabase> list) {
        save.databases(list);
    }
    
    /**
     * DorisTable
     */
    public void table(DorisTable entity) {
        save.table(entity);
    }
    
    public void tables(List<DorisTable> list) {
        save.tables(list);
    }
    
    /**
     * DorisColumn
     */
    public void column(DorisColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<DorisColumn> list) {
        save.columns(list);
    }
}