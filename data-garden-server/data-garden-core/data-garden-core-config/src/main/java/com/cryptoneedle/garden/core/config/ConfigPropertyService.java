package com.cryptoneedle.garden.core.config;

import com.cryptoneedle.garden.core.crud.config.*;

/**
 * <p>description: 配置-属性配置-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-08
 */
public class ConfigPropertyService {

    public final AddConfigService addConfigService;
    public final SelectConfigService selectConfigService;
    public final SaveConfigService saveConfigService;
    public final DeleteConfigService deleteConfigService;
    public final PatchConfigService patchConfigService;

    public ConfigPropertyService(AddConfigService addConfigService,
                                 SelectConfigService selectConfigService,
                                 SaveConfigService saveConfigService,
                                 DeleteConfigService deleteConfigService,
                                 PatchConfigService patchConfigService) {
        this.addConfigService = addConfigService;
        this.selectConfigService = selectConfigService;
        this.saveConfigService = saveConfigService;
        this.deleteConfigService = deleteConfigService;
        this.patchConfigService = patchConfigService;
    }

}