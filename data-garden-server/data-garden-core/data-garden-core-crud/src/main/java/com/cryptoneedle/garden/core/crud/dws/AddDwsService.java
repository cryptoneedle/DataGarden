package com.cryptoneedle.garden.core.crud.dws;


import com.cryptoneedle.garden.infrastructure.entity.dws.DwsColumn;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsTable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 新增数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class AddDwsService {
    
    private final SelectDwsService select;
    private final SaveDwsService save;
    
    public AddDwsService(SelectDwsService selectDwsService,
                         SaveDwsService saveDwsService) {
        this.select = selectDwsService;
        this.save = saveDwsService;
    }
    
    /**
     * DwsTable
     */
    public void table(DwsTable entity) {
        save.table(entity);
    }
    
    public void tables(List<DwsTable> list) {
        save.tables(list);
    }
    
    /**
     * DwsColumn
     */
    public void column(DwsColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<DwsColumn> list) {
        save.columns(list);
    }
}