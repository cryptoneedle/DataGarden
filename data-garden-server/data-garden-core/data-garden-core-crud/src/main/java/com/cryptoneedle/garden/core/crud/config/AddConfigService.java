package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description: 新增属性配置服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class AddConfigService {

    private final SaveConfigService saveConfigService;
    private final ConfigPropertyRepository configPropertyRepository;

    public AddConfigService(SaveConfigService saveConfigService,
                            ConfigPropertyRepository configPropertyRepository) {
        this.saveConfigService = saveConfigService;
        this.configPropertyRepository = configPropertyRepository;
    }

    /**
     * ConfigProperty
     */
    public void property(ConfigProperty entity) {
        saveConfigService.property(entity);
    }

    public void properties(List<ConfigProperty> list) {
        saveConfigService.properties(list);
    }
}