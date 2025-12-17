package com.cryptoneedle.garden.core.crud.dwd;


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
public class PatchDwdService {
    
    private final SelectDwdService select;
    private final SaveDwdService save;
    
    public PatchDwdService(SelectDwdService selectDwdService,
                         SaveDwdService saveDwdService) {
        this.select = selectDwdService;
        this.save = saveDwdService;
    }
    
    /**
     * DwdTable
     */
    
    /**
     * DwdColumn
     */
}