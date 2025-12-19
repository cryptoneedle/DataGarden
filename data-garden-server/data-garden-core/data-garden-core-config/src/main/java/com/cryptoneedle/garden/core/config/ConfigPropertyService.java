package com.cryptoneedle.garden.core.config;

import com.cryptoneedle.garden.core.crud.config.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 配置-属性配置-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-08
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class ConfigPropertyService {
    
    public final AddConfigService add;
    public final SelectConfigService select;
    public final SaveConfigService save;
    public final DeleteConfigService delete;
    public final PatchConfigService patch;
    
    public ConfigPropertyService(AddConfigService addConfigService,
                                 SelectConfigService selectConfigService,
                                 SaveConfigService saveConfigService,
                                 DeleteConfigService deleteConfigService,
                                 PatchConfigService patchConfigService) {
        this.add = addConfigService;
        this.select = selectConfigService;
        this.save = saveConfigService;
        this.delete = deleteConfigService;
        this.patch = patchConfigService;
    }
}