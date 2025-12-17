package com.cryptoneedle.garden.core.crud.dwd;


import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdColumn;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdTable;
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
public class AddDwdService {
    
    private final SelectDwdService select;
    private final SaveDwdService save;
    
    public AddDwdService(SelectDwdService selectDwdService,
                         SaveDwdService saveDwdService) {
        this.select = selectDwdService;
        this.save = saveDwdService;
    }
    
    /**
     * DwdTable
     */
    public void table(DwdTable entity) {
        save.table(entity);
    }
    
    public void tables(List<DwdTable> list) {
        save.tables(list);
    }
    
    /**
     * DwdColumn
     */
    public void column(DwdColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<DwdColumn> list) {
        save.columns(list);
    }
}