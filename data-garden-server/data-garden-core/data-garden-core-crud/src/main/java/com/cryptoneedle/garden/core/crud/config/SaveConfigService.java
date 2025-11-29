package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
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

    public SaveConfigService(ConfigPropertyRepository configPropertyRepository) {
        this.configPropertyRepository = configPropertyRepository;
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
}