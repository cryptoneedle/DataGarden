package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.common.enums.ConfigPropertyType;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.config.ConfigPropertyKey;
import com.cryptoneedle.garden.common.key.config.ConfigSshKey;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.vo.config.ConfigPropertyUpdateVo;
import com.cryptoneedle.garden.infrastructure.vo.config.ConfigSshUpdateVo;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
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
    
    private final SelectConfigService select;
    private final SaveConfigService save;
    
    public PatchConfigService(SelectConfigService selectConfigService,
                              SaveConfigService saveConfigService) {
        this.select = selectConfigService;
        this.save = saveConfigService;
    }
    
    /**
     * ConfigProperty
     */
    public void property(ConfigPropertyUpdateVo vo) throws EntityNotFoundException {
        ConfigPropertyKey key = ConfigPropertyKey.builder().propertyName(vo.getPropertyName()).build();
        ConfigProperty entity = select.propertyCheck(key);
        
        if (StringUtils.isNotBlank(vo.getValue())) {
            entity.setValue(vo.getValue())
                  .setType(ConfigPropertyType.MANUAL);
        }
        if (StringUtils.isNotBlank(vo.getComment())) {
            entity.setComment(vo.getComment())
                  .setType(ConfigPropertyType.MANUAL);
        }
        
        save.property(entity);
    }
    
    /**
     * ConfigSsh
     */
    public void ssh(@Valid ConfigSshUpdateVo vo) throws EntityNotFoundException {
        ConfigSshKey key = ConfigSshKey.builder().host(vo.getHost()).build();
        ConfigSsh entity = select.sshCheck(key);
        
        if (vo.getPort() != null) {
            entity.setPort(vo.getPort());
        }
        if (StringUtils.isNotBlank(vo.getUsername())) {
            entity.setUsername(vo.getUsername());
        }
        if (StringUtils.isNotBlank(vo.getPassword())) {
            entity.setPassword(vo.getPassword());
        }
        
        save.ssh(entity);
    }
}