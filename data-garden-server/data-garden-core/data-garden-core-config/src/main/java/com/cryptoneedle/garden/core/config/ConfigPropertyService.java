package com.cryptoneedle.garden.core.config;

import com.cryptoneedle.garden.common.enums.ConfigPropertyType;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.config.ConfigPropertyKey;
import com.cryptoneedle.garden.common.vo.config.ConfigPropertyAddVo;
import com.cryptoneedle.garden.common.vo.config.ConfigPropertyUpdateVo;
import com.cryptoneedle.garden.core.crud.config.*;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>description: 配置-属性配置-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-08
 */
@Service
public class ConfigPropertyService {

    public final AddConfigService add;
    public final SelectConfigService select;
    public final SaveConfigService save;
    public final DeleteConfigService delete;
    public final PatchConfigService patch;

    public ConfigPropertyService(AddConfigService addConfigService,
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

    public void add(ConfigPropertyAddVo vo) {
        ConfigPropertyKey key = ConfigPropertyKey.builder().propertyName(vo.getPropertyName()).build();
        ConfigProperty configProperty = select.property(key);
        if (configProperty != null) {
            throw new RuntimeException("属性配置: " + key + "已存在");
        }
        
        ConfigProperty entity = ConfigProperty.builder()
                .id(key)
                .value(vo.getValue())
                .comment(vo.getComment())
                .type(ConfigPropertyType.MANUAL)
                .build();
        add.property(entity);
    }

    public void modify(ConfigPropertyUpdateVo vo) throws EntityNotFoundException {
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

    public void delete(ConfigPropertyKey key) throws EntityNotFoundException {
        ConfigProperty entity = select.propertyCheck(key);
        if (ConfigPropertyType.AUTO.equals(entity.getType())) {
            throw new RuntimeException("禁止删除自动配置的属性");
        }
        
        delete.property(entity);
    }
}