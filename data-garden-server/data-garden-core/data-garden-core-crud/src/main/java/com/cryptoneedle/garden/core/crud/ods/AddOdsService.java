package com.cryptoneedle.garden.core.crud.ods;


import com.cryptoneedle.garden.infrastructure.entity.ads.AdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsTable;
import com.cryptoneedle.garden.infrastructure.repository.ads.AdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ads.AdsTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description: 新增数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class AddOdsService {

    private final SaveOdsService saveAdsService;
    private final AdsTableRepository adsTableRepository;
    private final AdsColumnRepository adsColumnRepository;

    public AddOdsService(SaveOdsService saveAdsService,
                         AdsTableRepository adsTableRepository,
                         AdsColumnRepository adsColumnRepository) {
        this.saveAdsService = saveAdsService;
        this.adsTableRepository = adsTableRepository;
        this.adsColumnRepository = adsColumnRepository;
    }

    /**
     * AdsTable
     */
    public void table(AdsTable entity) {
        saveAdsService.table(entity);
    }

    public void tables(List<AdsTable> list) {
        saveAdsService.tables(list);
    }

    /**
     * AdsColumn
     */
    public void column(AdsColumn entity) {
        saveAdsService.column(entity);
    }

    public void columns(List<AdsColumn> list) {
        saveAdsService.columns(list);
    }
}