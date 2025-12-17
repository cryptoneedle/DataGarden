package com.cryptoneedle.garden.core.crud.dws;


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
public class PatchDwsService {
    
    private final SelectDwsService select;
    private final SaveDwsService save;
    
    public PatchDwsService(SelectDwsService selectDwsService,
                         SaveDwsService saveDwsService) {
        this.select = selectDwsService;
        this.save = saveDwsService;
    }
    
    /**
     * DwsTable
     */
    
    /**
     * DwsColumn
     */
}