package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigSshRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 部分更新属性配置服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchConfigService {

    private final ConfigPropertyRepository configPropertyRepository;
    private final ConfigSshRepository configSshRepository;

    public PatchConfigService(ConfigPropertyRepository configPropertyRepository,
                               ConfigSshRepository configSshRepository) {
        this.configPropertyRepository = configPropertyRepository;
        this.configSshRepository = configSshRepository;
    }

    /**
     * ConfigProperty
     */

    /**
     * ConfigSsh
     */
}