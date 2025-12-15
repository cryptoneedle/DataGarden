package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.ads.SaveAdsService;
import com.cryptoneedle.garden.core.crud.config.SaveConfigService;
import com.cryptoneedle.garden.core.crud.dim.SaveDimService;
import com.cryptoneedle.garden.core.crud.doris.SaveDorisService;
import com.cryptoneedle.garden.core.crud.dwd.SaveDwdService;
import com.cryptoneedle.garden.core.crud.dws.SaveDwsService;
import com.cryptoneedle.garden.core.crud.ods.SaveOdsService;
import com.cryptoneedle.garden.core.crud.source.SaveSourceService;
import com.cryptoneedle.garden.core.crud.standard.SaveStandardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 保存数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SaveService {
    
    public final SaveConfigService config;
    public final SaveSourceService source;
    public final SaveDorisService doris;
    public final SaveDimService dim;
    public final SaveStandardService standard;
    public final SaveOdsService ods;
    public final SaveDwdService dwd;
    public final SaveDwsService dws;
    public final SaveAdsService ads;
    
    public SaveService(SaveConfigService config,
                       SaveSourceService source,
                       SaveDorisService doris,
                       SaveDimService dim,
                       SaveStandardService standard,
                       SaveOdsService ods,
                       SaveDwdService dwd,
                       SaveDwsService dws,
                       SaveAdsService ads) {
        this.config = config;
        this.source = source;
        this.doris = doris;
        this.dim = dim;
        this.standard = standard;
        this.ods = ods;
        this.dwd = dwd;
        this.dws = dws;
        this.ads = ads;
    }
}