package com.cryptoneedle.garden.infrastructure.entity.source;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.enums.SourceTableType;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
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
@Builder
@Accessors(chain = true)
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "source_table")
@Comment("数据源-数据库")
public class SourceTable extends BaseEntity {

    @EmbeddedId
    private SourceTableKey id;

    @Column(columnDefinition = "TEXT")
    @Comment("表说明")
    private String comment;
    @Enumerated(EnumType.STRING)
    @Comment("表类型")
    private SourceTableType tableType;

    @Comment("Doris目录")
    private String dorisCatalog;
    @Comment("系统编码 (会覆盖目录、数据库配置)")
    private String systemCode;
    @Comment("维度")
    private String dimension;
    @Enumerated(EnumType.STRING)
    @Comment("采集频率 (会覆盖目录、数据库配置)")
    private SourceCollectFrequencyType collectFrequency;
    @Comment("采集频率对应的时间点 (会覆盖目录、数据库配置) (每天为具体小时开始，每小时为具体分钟开始，每分钟为具体分开始)")
    private Integer collectTimePoint;
    @Comment("采集任务分组序号")
    private Integer collectGroupNum;

    @Comment("表")
    private String transTableName;
    @Column(columnDefinition = "TEXT")
    @Comment("表说明")
    private String transComment;
    @Comment("分桶数量")
    private Integer transBucketNum;

    @Comment("字段数量")
    private Integer columnNum;
    @Comment("数据量")
    private Long rowNum;
    @Comment("预估占用空间(格式化)")
    private String storageSpaceFormat;
    @Column(name = "storage_mb", precision = 30, scale = 20)
    @Comment("预估占用空间(单位：MBytes)")
    private BigDecimal storageMegaBytes;
    @Column(precision = 30, scale = 20)
    @Comment("行平均占用空间(单位：Byte)")
    private BigDecimal avgRowBytes;
    @Comment("统计时间")
    private LocalDateTime statisticDt;

    @Comment("锁定表")
    private boolean transTableLocked = false;
    @Comment("锁定说明")
    private boolean transCommentLocked = false;

    @Comment("启用")
    private boolean enabled = false;
}