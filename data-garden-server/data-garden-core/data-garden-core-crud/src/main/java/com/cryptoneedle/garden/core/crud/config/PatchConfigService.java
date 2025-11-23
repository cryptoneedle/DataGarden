package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
import org.springframework.stereotype.Service;

/**
 * <p>description: 部分更新属性配置服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class PatchConfigService {

    private final ConfigPropertyRepository configPropertyRepository;

    public PatchConfigService(ConfigPropertyRepository configPropertyRepository) {
        this.configPropertyRepository = configPropertyRepository;
    }

    /**
     * ConfigProperty
     */
}