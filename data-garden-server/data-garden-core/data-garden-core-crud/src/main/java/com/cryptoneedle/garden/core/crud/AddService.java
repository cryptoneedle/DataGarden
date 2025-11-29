package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.ads.AddAdsService;
import com.cryptoneedle.garden.core.crud.config.AddConfigService;
import com.cryptoneedle.garden.core.crud.dim.AddDimService;
import com.cryptoneedle.garden.core.crud.doris.AddDorisService;
import com.cryptoneedle.garden.core.crud.dwd.AddDwdService;
import com.cryptoneedle.garden.core.crud.dws.AddDwsService;
import com.cryptoneedle.garden.core.crud.ods.AddOdsService;
import com.cryptoneedle.garden.core.crud.source.AddSourceService;
import com.cryptoneedle.garden.core.crud.standard.AddStandardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 新增数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class AddService {

    public final AddConfigService config;
    public final AddSourceService source;
    public final AddDorisService doris;
    public final AddDimService dim;
    public final AddStandardService standard;
    public final AddOdsService ods;
    public final AddDwdService dwd;
    public final AddDwsService dws;
    public final AddAdsService ads;

    public AddService(AddConfigService config,
                      AddSourceService source,
                      AddDorisService doris,
                      AddDimService dim,
                      AddStandardService standard,
                      AddOdsService ods,
                      AddDwdService dwd,
                      AddDwsService dws,
                      AddAdsService ads) {
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