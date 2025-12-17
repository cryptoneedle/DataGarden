package com.cryptoneedle.garden.core.crud.ods;


import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
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
public class AddOdsService {
    
    private final SelectOdsService select;
    private final SaveOdsService save;
    
    public AddOdsService(SelectOdsService selectOdsService,
                         SaveOdsService saveOdsService) {
        this.select = selectOdsService;
        this.save = saveOdsService;
    }
    
    /**
     * OdsTable
     */
    public void table(OdsTable entity) {
        save.table(entity);
    }
    
    public void tables(List<OdsTable> list) {
        save.tables(list);
    }
    
    /**
     * OdsColumn
     */
    public void column(OdsColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<OdsColumn> list) {
        save.columns(list);
    }
}