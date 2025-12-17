package com.cryptoneedle.garden.core.crud.ads;


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
public class PatchAdsService {
    
    private final SelectAdsService select;
    private final SaveAdsService save;
    
    public PatchAdsService(SelectAdsService selectAdsService,
                         SaveAdsService saveAdsService) {
        this.select = selectAdsService;
        this.save = saveAdsService;
    }
    
    /**
     * AdsTable
     */
    
    /**
     * AdsColumn
     */
}