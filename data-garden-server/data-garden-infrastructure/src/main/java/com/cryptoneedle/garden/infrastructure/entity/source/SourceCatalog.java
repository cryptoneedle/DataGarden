package com.cryptoneedle.garden.infrastructure.entity.source;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Strings;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * <p>description: 数据源-目录-实体 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
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
@Table(name = "source_catalog")
@Comment("数据源-目录")
public class SourceCatalog extends BaseEntity {
    
    @EmbeddedId
    private SourceCatalogKey id;
    
    @Comment("Doris目录(唯一)")
    private String dorisCatalog;
    @Comment("默认系统编码")
    private String systemCode;
    @Enumerated(EnumType.STRING)
    @Comment("默认采集频率")
    private SourceCollectFrequencyType collectFrequency;
    @Comment("默认采集频率对应的时间点 (每天为具体小时开始，每小时为具体分钟开始，每分钟为具体分开始)")
    private Integer collectTimePoint;
    
    @Comment("主机")
    private String host;
    @Comment("端口")
    private Integer port;
    @Comment("数据库类型标识 (通过SPI动态支持)")
    private String databaseType;
    @Comment("连接类型 (通过SPI动态支持)")
    private String connectType;
    @Comment("路径")
    private String route;
    @Comment("用户名")
    private String username;
    @Comment("密码")
    private String password;
    
    @Comment("URL")
    private String url;
    @Comment("数据库版本")
    private String version;
    @Comment("服务器可连接")
    private Boolean serverConnected = false;
    @Comment("Jdbc可连接")
    private Boolean jdbcConnected = false;
    @Comment("Doris可连接")
    private Boolean dorisConnected = false;
    @Comment("服务器最后可连接时间")
    private LocalDateTime serverConnectedDt;
    @Comment("Jdbc最后可连接时间")
    private LocalDateTime jdbcConnectedDt;
    @Comment("Doris最后可连接时间")
    private LocalDateTime dorisConnectedDt;
    
    @Comment("启用")
    private Boolean enabled = false;
    
    @Comment("SSH主机")
    private String sshHost;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sshHost", referencedColumnName = "host", insertable = false, updatable = false)
    @Comment("SSH配置关联")
    private ConfigSsh configSsh;
    
    public boolean equalsJdbc(SourceCatalog other) {
        boolean basicEquals = Strings.CI.equals(this.host, other.getHost())
                && this.port.equals(other.getPort())
                && Strings.CI.equals(this.databaseType, other.getDatabaseType())
                && Strings.CI.equals(this.connectType, other.getConnectType())
                && Strings.CI.equals(this.route, other.getRoute())
                && Strings.CI.equals(this.username, other.getUsername())
                && Strings.CI.equals(this.password, other.getPassword());
        
        // 检查SSH配置是否相同
        if (this.configSsh == null && other.getConfigSsh() == null) {
            return basicEquals;
        }
        if (this.configSsh == null || other.getConfigSsh() == null) {
            return false;
        }
        return basicEquals && this.configSsh.equalsConnect(other.getConfigSsh());
    }
    
    public String address() {
        return this.host+ ":" + this.port;
    }
}