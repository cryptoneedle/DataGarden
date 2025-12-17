package com.cryptoneedle.garden.core.crud.ads;


import com.cryptoneedle.garden.infrastructure.entity.ads.AdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsTable;
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
public class AddAdsService {
    
    private final SelectAdsService select;
    private final SaveAdsService save;
    
    public AddAdsService(SelectAdsService selectAdsService,
                         SaveAdsService saveAdsService) {
        this.select = selectAdsService;
        this.save = saveAdsService;
    }
    
    /**
     * AdsTable
     */
    public void table(AdsTable entity) {
        save.table(entity);
    }
    
    public void tables(List<AdsTable> list) {
        save.tables(list);
    }
    
    /**
     * AdsColumn
     */
    public void column(AdsColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<AdsColumn> list) {
        save.columns(list);
    }
}