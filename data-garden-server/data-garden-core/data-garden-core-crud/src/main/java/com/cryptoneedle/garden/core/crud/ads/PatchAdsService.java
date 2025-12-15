package com.cryptoneedle.garden.core.crud.ads;


import com.cryptoneedle.garden.infrastructure.repository.ads.AdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ads.AdsTableRepository;
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
    
    private final AdsTableRepository adsTableRepository;
    private final AdsColumnRepository adsColumnRepository;
    
    public PatchAdsService(AdsTableRepository adsTableRepository,
                           AdsColumnRepository adsColumnRepository) {
        this.adsTableRepository = adsTableRepository;
        this.adsColumnRepository = adsColumnRepository;
    }
    
    /**
     * AdsTable
     */
    
    /**
     * AdsColumn
     */
}