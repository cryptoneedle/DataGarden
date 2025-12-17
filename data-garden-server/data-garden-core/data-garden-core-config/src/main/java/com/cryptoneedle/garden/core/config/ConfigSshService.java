package com.cryptoneedle.garden.core.config;

import com.cryptoneedle.garden.core.crud.config.*;
import org.springframework.stereotype.Service;

/**
 * <p>description: 配置-隧道配置-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-08
 */
@Service
public class ConfigSshService {
    
    public final AddConfigService add;
    public final SelectConfigService select;
    public final SaveConfigService save;
    public final DeleteConfigService delete;
    public final PatchConfigService patch;
    
    public ConfigSshService(AddConfigService addConfigService,
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