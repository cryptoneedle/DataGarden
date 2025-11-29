package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.ads.DeleteAdsService;
import com.cryptoneedle.garden.core.crud.config.DeleteConfigService;
import com.cryptoneedle.garden.core.crud.dim.DeleteDimService;
import com.cryptoneedle.garden.core.crud.doris.DeleteDorisService;
import com.cryptoneedle.garden.core.crud.dwd.DeleteDwdService;
import com.cryptoneedle.garden.core.crud.dws.DeleteDwsService;
import com.cryptoneedle.garden.core.crud.ods.DeleteOdsService;
import com.cryptoneedle.garden.core.crud.source.DeleteSourceService;
import com.cryptoneedle.garden.core.crud.standard.DeleteStandardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 删除数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DeleteService {

    public final DeleteConfigService config;
    public final DeleteSourceService source;
    public final DeleteDorisService doris;
    public final DeleteDimService dim;
    public final DeleteStandardService standard;
    public final DeleteOdsService ods;
    public final DeleteDwdService dwd;
    public final DeleteDwsService dws;
    public final DeleteAdsService ads;

    public DeleteService(DeleteConfigService config,
                         DeleteSourceService source,
                         DeleteDorisService doris,
                         DeleteDimService dim,
                         DeleteStandardService standard,
                         DeleteOdsService ods,
                         DeleteDwdService dwd,
                         DeleteDwsService dws,
                         DeleteAdsService ads) {
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