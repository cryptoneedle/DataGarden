package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.common.enums.ConfigPropertyType;
import com.cryptoneedle.garden.common.key.config.ConfigPropertyKey;
import com.cryptoneedle.garden.common.key.config.ConfigSshKey;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.vo.config.ConfigPropertyAddVo;
import com.cryptoneedle.garden.infrastructure.vo.config.ConfigSshAddVo;
import jakarta.validation.Valid;
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
    
    private final SelectConfigService select;
    private final SaveConfigService save;
    
    public AddConfigService(SelectConfigService selectConfigService,
                            SaveConfigService saveConfigService) {
        this.select = selectConfigService;
        this.save = saveConfigService;
    }
    
    /**
     * ConfigProperty
     */
    public void property(ConfigProperty entity) {
        save.property(entity);
    }
    
    public void property(ConfigPropertyAddVo vo) {
        ConfigPropertyKey key = ConfigPropertyKey.builder().propertyName(vo.getPropertyName()).build();
        ConfigProperty entity = select.property(key);
        if (entity != null) {
            throw new RuntimeException("属性配置: " + key + "已存在");
        }
        
        entity = ConfigProperty.builder()
                               .id(key)
                               .value(vo.getValue())
                               .comment(vo.getComment())
                               .type(ConfigPropertyType.MANUAL)
                               .build();
        save.property(entity);
    }
    
    public void properties(List<ConfigProperty> list) {
        save.properties(list);
    }
    
    /**
     * ConfigSsh
     */
    public void ssh(ConfigSsh entity) {
        save.ssh(entity);
    }
    
    public void ssh(@Valid ConfigSshAddVo vo) {
        ConfigSshKey key = ConfigSshKey.builder().host(vo.getHost()).build();
        ConfigSsh entity = select.ssh(key);
        if (entity != null) {
            throw new RuntimeException("隧道配置: " + key + "已存在");
        }
        
        entity = ConfigSsh.builder()
                          .id(key)
                          .port(vo.getPort())
                          .username(vo.getUsername())
                          .password(vo.getPassword())
                          .build();
        save.ssh(entity);
    }
    
    public void sshs(List<ConfigSsh> list) {
        save.sshs(list);
    }
}