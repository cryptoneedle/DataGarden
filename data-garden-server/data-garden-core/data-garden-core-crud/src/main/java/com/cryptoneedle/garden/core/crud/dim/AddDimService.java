package com.cryptoneedle.garden.core.crud.dim;


import com.cryptoneedle.garden.infrastructure.entity.dim.DimColumn;
import com.cryptoneedle.garden.infrastructure.entity.dim.DimTable;
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
public class AddDimService {
    
    private final SelectDimService select;
    private final SaveDimService save;
    
    public AddDimService(SelectDimService selectDimService,
                         SaveDimService saveDimService) {
        this.select = selectDimService;
        this.save = saveDimService;
    }
    
    /**
     * DimTable
     */
    public void table(DimTable entity) {
        save.table(entity);
    }
    
    public void tables(List<DimTable> list) {
        save.tables(list);
    }
    
    /**
     * DimColumn
     */
    public void column(DimColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<DimColumn> list) {
        save.columns(list);
    }
}