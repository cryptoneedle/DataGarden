package com.cryptoneedle.garden.infrastructure.repository.config;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.config.ConfigPropertyKey;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 配置-属性配置-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface ConfigPropertyRepository extends BaseRepository<ConfigProperty, ConfigPropertyKey> {
    
    @Query("""
             FROM ConfigProperty
            ORDER BY id.propertyName
            """)
    List<ConfigProperty> properties();
}