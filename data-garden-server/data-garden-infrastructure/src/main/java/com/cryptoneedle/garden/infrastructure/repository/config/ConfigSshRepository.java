package com.cryptoneedle.garden.infrastructure.repository.config;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.config.ConfigSshKey;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>description: 配置-隧道配置-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface ConfigSshRepository extends BaseRepository<ConfigSsh, ConfigSshKey> {

    @Query("""
             FROM ConfigSsh
            ORDER BY enabled DESC, id.host
            """)
    List<ConfigSsh> sshs();
}