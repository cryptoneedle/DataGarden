package com.cryptoneedle.garden.core.crud.ads;


import com.cryptoneedle.garden.infrastructure.entity.ads.AdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsTable;
import com.cryptoneedle.garden.infrastructure.repository.ads.AdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ads.AdsTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 删除数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DeleteAdsService {
    
    private final AdsTableRepository adsTableRepository;
    private final AdsColumnRepository adsColumnRepository;
    
    public DeleteAdsService(AdsTableRepository adsTableRepository,
                            AdsColumnRepository adsColumnRepository) {
        this.adsTableRepository = adsTableRepository;
        this.adsColumnRepository = adsColumnRepository;
    }
    
    /**
     * AdsTable
     */
    public void table(AdsTable entity) {
        adsTableRepository.delete(entity);
    }
    
    public void tables(List<AdsTable> list) {
        adsTableRepository.deleteAll(list);
    }
    
    /**
     * AdsColumn
     */
    public void column(AdsColumn entity) {
        adsColumnRepository.delete(entity);
    }
    
    public void columns(List<AdsColumn> list) {
        adsColumnRepository.deleteAll(list);
    }
}