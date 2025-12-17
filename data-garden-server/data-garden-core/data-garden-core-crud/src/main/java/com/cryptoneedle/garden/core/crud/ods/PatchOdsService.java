package com.cryptoneedle.garden.core.crud.ods;


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
public class PatchOdsService {
    
    private final SelectOdsService select;
    private final SaveOdsService save;
    
    public PatchOdsService(SelectOdsService selectOdsService,
                         SaveOdsService saveOdsService) {
        this.select = selectOdsService;
        this.save = saveOdsService;
    }
    
    /**
     * OdsTable
     */
    
    /**
     * OdsColumn
     */
}