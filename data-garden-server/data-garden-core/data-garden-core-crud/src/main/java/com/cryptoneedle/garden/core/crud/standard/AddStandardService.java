package com.cryptoneedle.garden.core.crud.standard;


import com.cryptoneedle.garden.infrastructure.entity.standard.StandardColumn;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardTable;
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
public class AddStandardService {
    
    private final SelectStandardService select;
    private final SaveStandardService save;
    
    public AddStandardService(SelectStandardService selectStandardService,
                         SaveStandardService saveStandardService) {
        this.select = selectStandardService;
        this.save = saveStandardService;
    }
    
    /**
     * StandardTable
     */
    public void table(StandardTable entity) {
        save.table(entity);
    }
    
    public void tables(List<StandardTable> list) {
        save.tables(list);
    }
    
    /**
     * StandardColumn
     */
    public void column(StandardColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<StandardColumn> list) {
        save.columns(list);
    }
}