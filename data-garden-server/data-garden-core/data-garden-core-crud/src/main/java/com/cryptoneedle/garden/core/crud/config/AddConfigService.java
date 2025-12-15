package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigSshRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 新增属性配置服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class AddConfigService {
    
    private final SaveConfigService saveConfigService;
    private final ConfigPropertyRepository configPropertyRepository;
    private final ConfigSshRepository configSshRepository;
    
    public AddConfigService(SaveConfigService saveConfigService,
                            ConfigPropertyRepository configPropertyRepository,
                            ConfigSshRepository configSshRepository) {
        this.saveConfigService = saveConfigService;
        this.configPropertyRepository = configPropertyRepository;
        this.configSshRepository = configSshRepository;
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
    
    /**
     * ConfigSsh
     */
    public void ssh(ConfigSsh entity) {
        saveConfigService.ssh(entity);
    }
    
    public void sshs(List<ConfigSsh> list) {
        saveConfigService.sshs(list);
    }
}