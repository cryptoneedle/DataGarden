package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.ads.PatchAdsService;
import com.cryptoneedle.garden.core.crud.config.PatchConfigService;
import com.cryptoneedle.garden.core.crud.dim.PatchDimService;
import com.cryptoneedle.garden.core.crud.doris.PatchDorisService;
import com.cryptoneedle.garden.core.crud.dwd.PatchDwdService;
import com.cryptoneedle.garden.core.crud.dws.PatchDwsService;
import com.cryptoneedle.garden.core.crud.mapping.PatchMappingService;
import com.cryptoneedle.garden.core.crud.ods.PatchOdsService;
import com.cryptoneedle.garden.core.crud.source.PatchSourceService;
import com.cryptoneedle.garden.core.crud.standard.PatchStandardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 部分更新数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchService {
    
    public final PatchConfigService config;
    public final PatchSourceService source;
    public final PatchDorisService doris;
    public final PatchDimService dim;
    public final PatchStandardService standard;
    public final PatchOdsService ods;
    public final PatchMappingService mapping;
    public final PatchDwdService dwd;
    public final PatchDwsService dws;
    public final PatchAdsService ads;
    
    public PatchService(PatchConfigService config,
                        PatchSourceService source,
                        PatchDorisService doris,
                        PatchDimService dim,
                        PatchStandardService standard,
                        PatchOdsService ods,
                        PatchMappingService mapping,
                        PatchDwdService dwd,
                        PatchDwsService dws,
                        PatchAdsService ads) {
        this.config = config;
        this.source = source;
        this.doris = doris;
        this.dim = dim;
        this.standard = standard;
        this.ods = ods;
        this.mapping = mapping;
        this.dwd = dwd;
        this.dws = dws;
        this.ads = ads;
    }
}