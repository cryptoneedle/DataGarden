package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.ads.SelectAdsService;
import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import com.cryptoneedle.garden.core.crud.dim.SelectDimService;
import com.cryptoneedle.garden.core.crud.doris.SelectDorisService;
import com.cryptoneedle.garden.core.crud.dwd.SelectDwdService;
import com.cryptoneedle.garden.core.crud.dws.SelectDwsService;
import com.cryptoneedle.garden.core.crud.ods.SelectOdsService;
import com.cryptoneedle.garden.core.crud.source.SelectSourceService;
import com.cryptoneedle.garden.core.crud.standard.SelectStandardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 查询数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SelectService {

    public final SelectConfigService config;
    public final SelectSourceService source;
    public final SelectDorisService doris;
    public final SelectDimService dim;
    public final SelectStandardService standard;
    public final SelectOdsService ods;
    public final SelectDwdService dwd;
    public final SelectDwsService dws;
    public final SelectAdsService ads;

    public SelectService(SelectConfigService config,
                         SelectSourceService source,
                         SelectDorisService doris,
                         SelectDimService dim,
                         SelectStandardService standard,
                         SelectOdsService ods,
                         SelectDwdService dwd,
                         SelectDwsService dws,
                         SelectAdsService ads) {
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