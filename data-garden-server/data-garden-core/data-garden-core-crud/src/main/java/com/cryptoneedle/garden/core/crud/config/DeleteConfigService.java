package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 删除属性配置服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DeleteConfigService {

    private final ConfigPropertyRepository configPropertyRepository;

    public DeleteConfigService(ConfigPropertyRepository configPropertyRepository) {
        this.configPropertyRepository = configPropertyRepository;
    }

    /**
     * ConfigProperty
     */

    public void property(ConfigProperty entity) {
        configPropertyRepository.delete(entity);
    }

    public void properties(List<ConfigProperty> list) {
        configPropertyRepository.deleteAll(list);
    }
}