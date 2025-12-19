package com.cryptoneedle.garden.infrastructure.entity.config;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.config.ConfigSshKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

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
    
    @OneToMany(mappedBy = "configSsh", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<SourceCatalog> catalogs;
    
    public boolean equalsConnect(ConfigSsh other) {
        return this.id.equals(other.id)
                && this.port == other.port
                && this.username.equals(other.username)
                && this.password.equals(other.password);
    }
    
    public String address() {
        return this.id.getHost() + ":" + this.port;
    }
}