package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.config.ConfigPropertyKey;
import com.cryptoneedle.garden.common.key.config.ConfigSshKey;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigSshRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 查询属性配置服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SelectConfigService {
    
    private final ConfigPropertyRepository configPropertyRepository;
    private final ConfigSshRepository configSshRepository;
    
    public SelectConfigService(ConfigPropertyRepository configPropertyRepository,
                               ConfigSshRepository configSshRepository) {
        this.configPropertyRepository = configPropertyRepository;
        this.configSshRepository = configSshRepository;
    }
    
    /**
     * ConfigProperty
     */
    public ConfigProperty property(ConfigPropertyKey id) {
        return configPropertyRepository.findById(id).orElse(null);
    }
    
    public ConfigProperty property(String propertyName) {
        return property(ConfigPropertyKey.builder().propertyName(propertyName).build());
    }
    
    public ConfigProperty propertyCheck(ConfigPropertyKey id) throws EntityNotFoundException {
        return configPropertyRepository.findById(id)
                                       .orElseThrow(() -> new EntityNotFoundException("ConfigProperty", id.toString()));
    }
    
    public List<ConfigProperty> properties() {
        return configPropertyRepository.properties();
    }
    
    /**
     * ConfigSsh
     */
    public ConfigSsh ssh(ConfigSshKey id) {
        return configSshRepository.findById(id).orElse(null);
    }
    
    public ConfigSsh ssh(String host) {
        return ssh(ConfigSshKey.builder().host(host).build());
    }
    
    public ConfigSsh sshCheck(ConfigSshKey id) throws EntityNotFoundException {
        return configSshRepository.findById(id)
                                  .orElseThrow(() -> new EntityNotFoundException("ConfigSsh", id.toString()));
    }
    
    public List<ConfigSsh> sshs() {
        return configSshRepository.sshs();
    }
}