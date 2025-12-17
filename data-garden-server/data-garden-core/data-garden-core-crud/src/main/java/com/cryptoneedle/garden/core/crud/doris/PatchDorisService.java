package com.cryptoneedle.garden.core.crud.doris;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 部分更新Doris数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchDorisService {
    
    private final SelectDorisService select;
    private final SaveDorisService save;
    
    public PatchDorisService(SelectDorisService selectDorisService,
                           SaveDorisService saveDorisService) {
        this.select = selectDorisService;
        this.save = saveDorisService;
    }
    
    /**
     * DorisCatalog
     */
    
    /**
     * DorisDatabase
     */
    
    /**
     * DorisTable
     */
    
    /**
     * DorisColumn
     */
}