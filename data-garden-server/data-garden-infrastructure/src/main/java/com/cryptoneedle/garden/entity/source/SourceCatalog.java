package com.cryptoneedle.garden.entity.source;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.enums.SourceConnectType;
import com.cryptoneedle.garden.common.enums.SourceDatabaseType;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
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
@ToString
@Builder
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "source_catalog")
@IdClass(SourceCatalogKey.class)
@Comment("数据源-目录")
public class SourceCatalog extends BaseEntity {

    @Id
    @Comment("目录")
    private String catalog;

    @Comment("Doris目录")
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
    @Enumerated(EnumType.STRING)
    @Comment("数据库类型")
    private SourceDatabaseType databaseType;
    @Enumerated(EnumType.STRING)
    @Comment("连接类型")
    private SourceConnectType connectType;
    @Comment("路径")
    private String route;
    @Comment("驱动")
    private String driver;
    @Comment("用户名")
    private String username;
    @Comment("密码")
    private String password;

    @Comment("URL")
    private String url;
    @Comment("数据库版本")
    private String version;
    @Comment("服务器可连接")
    private Boolean serverConnected;
    @Comment("Jdbc可连接")
    private Boolean jdbcConnected;
    @Comment("Doris可连接")
    private Boolean dorisConnected;
    @Comment("服务器最后可连接时间")
    private LocalDateTime serverConnectedDt;
    @Comment("Jdbc最后可连接时间")
    private LocalDateTime jdbcConnectedDt;
    @Comment("Doris最后可连接时间")
    private LocalDateTime dorisConnectedDt;

    @Comment("启用")
    private Boolean enabled;

    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalog(this.catalog).build();
    }
}