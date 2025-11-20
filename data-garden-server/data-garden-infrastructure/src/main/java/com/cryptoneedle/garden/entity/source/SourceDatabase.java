package com.cryptoneedle.garden.entity.source;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * <p>description: 数据源-数据库-实体 </p>
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
@Table(name = "source_database")
@IdClass(SourceDatabaseKey.class)
@Comment("数据源-数据库")
public class SourceDatabase extends BaseEntity {

    @Id
    @Comment("目录")
    private String catalog;
    @Id
    @Comment("数据库")
    private String database;

    @Comment("启用")
    private Boolean enabled;

    @Comment("Doris目录")
    private String dorisCatalog;
    @Comment("默认系统编码 (会覆盖目录配置)")
    private String systemCode;
    @Enumerated(EnumType.STRING)
    @Comment("默认采集频率 (会覆盖目录配置)")
    private SourceCollectFrequencyType collectFrequency;
    @Comment("默认采集频率对应的时间点 (会覆盖目录配置) (每天为具体小时开始，每小时为具体分钟开始，每分钟为具体分开始)")
    private Integer collectTimePoint;

    @Comment("表数量")
    private Long tableNum;
    @Comment("视图数量")
    private Long viewNum;
    @Comment("统计时间")
    private LocalDateTime statisticDt;

    public SourceDatabaseKey sourceDatabaseKey() {
        return SourceDatabaseKey.builder().catalog(this.catalog).database(this.database).build();
    }
}