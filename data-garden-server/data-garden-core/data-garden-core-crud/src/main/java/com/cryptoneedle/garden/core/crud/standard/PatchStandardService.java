package com.cryptoneedle.garden.core.crud.standard;


import com.cryptoneedle.garden.infrastructure.repository.ads.AdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ads.AdsTableRepository;
import org.springframework.stereotype.Service;

/**
 * <p>description: 部分更新数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class PatchStandardService {

    private final AdsTableRepository adsTableRepository;
    private final AdsColumnRepository adsColumnRepository;

    public PatchStandardService(AdsTableRepository adsTableRepository,
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