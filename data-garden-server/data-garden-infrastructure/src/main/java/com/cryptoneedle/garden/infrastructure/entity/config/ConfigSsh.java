package com.cryptoneedle.garden.infrastructure.entity.config;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.config.ConfigSshKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: 配置-隧道配置-实体 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-29
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "config_ssh")
@Comment("配置-隧道配置")
public class ConfigSsh extends BaseEntity {

    @EmbeddedId
    @Comment("主机")
    private ConfigSshKey id;

    @Comment("端口")
    private int port = 22;

    @Comment("用户名")
    private String username;
    @Comment("密码")
    private String password;

    @Comment("启用")
    private boolean enabled = true;
}
