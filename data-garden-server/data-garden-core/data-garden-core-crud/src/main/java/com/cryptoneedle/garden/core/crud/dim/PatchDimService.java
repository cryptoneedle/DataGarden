package com.cryptoneedle.garden.core.crud.dim;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 部分更新数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchDimService {
    
    private final SelectDimService select;
    private final SaveDimService save;
    
    public PatchDimService(SelectDimService selectDimService,
                         SaveDimService saveDimService) {
        this.select = selectDimService;
        this.save = saveDimService;
    }
    
    /**
     * DimTable
     */
    
    /**
     * DimColumn
     */
}