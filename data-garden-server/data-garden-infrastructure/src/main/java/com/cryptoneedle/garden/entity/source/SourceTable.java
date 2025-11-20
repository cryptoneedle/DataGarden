package com.cryptoneedle.garden.entity.source;

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
@ToString
@Builder
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "source_table")
@IdClass(SourceTableKey.class)
@Comment("数据源-数据库")
public class SourceTable extends BaseEntity {

    @Id
    @Comment("目录")
    private String catalog;
    @Id
    @Comment("数据库")
    private String database;
    @Id
    @Comment("表")
    private String table;

    @Comment("启用")
    private Boolean enabled;

    @Column(columnDefinition = "TEXT")
    @Comment("表评论")
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
    @Comment("采集任务分片数")
    private Integer collectShardNum;

    @Comment("数据量")
    private Long rowNum;
    @Column(precision = 30, scale = 20)
    @Comment("行平均占用空间(单位：Byte)")
    private BigDecimal avgRowBytes;
    @Column(name = "storage_mb")
    @Comment("预估占用空间(单位：MBytes)")
    private BigDecimal storageMegaBytes;
    @Comment("预估占用空间(格式化)")
    private String storageFormat;
    @Comment("统计时间")
    private LocalDateTime statisticDt;

    @Comment("表")
    private String transTable;
    @Column(columnDefinition = "TEXT")
    @Comment("表评论")
    private String transComment;
    @Comment("锁定表")
    private Boolean transTableLocked;
    @Comment("锁定评论")
    private Boolean transCommentLocked;
    @Comment("预估分桶数量")
    private Integer transBucketNum;

    public SourceTableKey sourceTableKey() {
        return SourceTableKey.builder().catalog(this.catalog).database(this.database).table(this.table).build();
    }
}