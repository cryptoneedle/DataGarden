package com.cryptoneedle.garden.core.config;

import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.config.ConfigSshKey;
import com.cryptoneedle.garden.common.vo.config.ConfigSshAddVo;
import com.cryptoneedle.garden.common.vo.config.ConfigSshUpdateVo;
import com.cryptoneedle.garden.core.crud.config.*;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>description: 配置-隧道配置-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-08
 */
@Service
public class ConfigSshService {
    
    public final AddConfigService add;
    public final SelectConfigService select;
    public final SaveConfigService save;
    public final DeleteConfigService delete;
    public final PatchConfigService patch;
    
    public ConfigSshService(AddConfigService addConfigService,
                            SelectConfigService selectConfigService,
                            SaveConfigService saveConfigService,
                            DeleteConfigService deleteConfigService,
                            PatchConfigService patchConfigService) {
        this.add = addConfigService;
        this.select = selectConfigService;
        this.save = saveConfigService;
        this.delete = deleteConfigService;
        this.patch = patchConfigService;
    }
    
    public void add(ConfigSshAddVo vo) {
        ConfigSshKey key = ConfigSshKey.builder().host(vo.getHost()).build();
        ConfigSsh ssh = select.ssh(key);
        if (ssh != null) {
            throw new RuntimeException("隧道配置: " + ssh + "已存在");
        }
        
        ConfigSsh entity = ConfigSsh.builder()
                                    .id(key)
                                    .port(vo.getPort())
                                    .username(vo.getUsername())
                                    .password(vo.getPassword())
                                    .build();
        add.ssh(entity);
    }
    
    public void modify(ConfigSshUpdateVo vo) throws EntityNotFoundException {
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
    
    public void delete(ConfigSshKey key) throws EntityNotFoundException {
        ConfigSsh entity = select.sshCheck(key);
        delete.ssh(entity);
    }
}