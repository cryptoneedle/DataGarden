package com.cryptoneedle.garden.core.crud.standard;


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
public class PatchStandardService {
    
    private final SelectStandardService select;
    private final SaveStandardService save;
    
    public PatchStandardService(SelectStandardService selectStandardService,
                              SaveStandardService saveStandardService) {
        this.select = selectStandardService;
        this.save = saveStandardService;
    }
    
    /**
     * StandardTable
     */
    
    /**
     * StandardColumn
     */
}