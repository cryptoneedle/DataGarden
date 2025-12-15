package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigSshRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 保存属性配置服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SaveConfigService {
    
    private final ConfigPropertyRepository configPropertyRepository;
    private final ConfigSshRepository configSshRepository;
    
    public SaveConfigService(ConfigPropertyRepository configPropertyRepository,
                             ConfigSshRepository configSshRepository) {
        this.configPropertyRepository = configPropertyRepository;
        this.configSshRepository = configSshRepository;
    }
    
    /**
     * ConfigProperty
     */
    public void property(ConfigProperty entity) {
        configPropertyRepository.save(entity);
    }
    
    public void properties(List<ConfigProperty> list) {
        configPropertyRepository.saveAll(list);
    }
    
    /**
     * ConfigSsh
     */
    public void ssh(ConfigSsh entity) {
        configSshRepository.save(entity);
    }
    
    public void sshs(List<ConfigSsh> list) {
        configSshRepository.saveAll(list);
    }
}