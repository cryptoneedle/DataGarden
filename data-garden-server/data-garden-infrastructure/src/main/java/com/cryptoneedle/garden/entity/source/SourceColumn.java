package com.cryptoneedle.garden.entity.source;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.SourceColumnType;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>description: 数据源-字段-实体 </p>
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
@Table(name = "source_column")
@IdClass(SourceColumnKey.class)
@Comment("数据源-字段")
public class SourceColumn extends BaseEntity {

    @Id
    @Comment("目录")
    private String catalog;
    @Id
    @Comment("数据库")
    private String database;
    @Id
    @Comment("表")
    private String table;
    @Id
    @Comment("字段")
    private String column;

    @Comment("启用")
    private Boolean enabled;

    @Column(columnDefinition = "TEXT")
    @Comment("字段评论")
    private String comment;
    @Comment("排序")
    private Long position;
    @Enumerated(EnumType.STRING)
    @Comment("字段类型")
    private SourceColumnType columnType;

    @Comment("Doris目录")
    private String dorisCatalog;
    @Comment("系统编码 (会覆盖目录、数据库、表配置)")
    private String systemCode;

    @Comment("数据类型")
    private String dataType;
    @Comment("长度")
    private Long length;
    @Comment("精度")
    private Long precision;
    @Comment("标度")
    private Long scale;
    @Comment("数据量")
    private Long rowNum;
    @Comment("采样数据量")
    private Long sampleNum;
    @Comment("采样率")
    private BigDecimal sampleRate;
    @Comment("采样空值数据量")
    private Long nullNum;
    @Comment("采样基数")
    private Long distinctNum;
    @Column(precision = 21, scale = 20)
    @Comment("采样数据密度")
    private BigDecimal density;
    @Column(columnDefinition = "TEXT")
    @Comment("采样最小值")
    private String minValue;
    @Column(columnDefinition = "TEXT")
    @Comment("采样最大值")
    private String maxValue;
    @Column(precision = 30, scale = 20)
    @Comment("字段平均占用空间(单位：Byte)")
    private BigDecimal avgColumnBytes;
    @Column(name = "storage_mb")
    @Comment("预估占用空间(单位：MBytes)")
    private BigDecimal storageMegaBytes;
    @Comment("预估占用空间(格式化)")
    private String storageFormat;
    @Comment("统计时间")
    private LocalDateTime statisticDt;

    @Comment("表")
    private String transTable;
    @Comment("字段")
    private String transColumn;
    @Column(columnDefinition = "TEXT")
    @Comment("字段评论")
    private String transComment;
    @Comment("数据类型")
    private String transDataType;
    @Comment("长度")
    private Long transLength;
    @Comment("精度")
    private Long transPrecision;
    @Comment("标度")
    private Long transScale;
    @Comment("锁定字段")
    private Boolean transColumnLocked;
    @Comment("锁定评论")
    private Boolean transCommentLocked;
    @Comment("锁定数据类型")
    private String transDataTypeLocked;
    @Comment("锁定长度")
    private Long transLengthLocked;
    @Comment("锁定精度")
    private Long transPrecisionLocked;
    @Comment("锁定标度")
    private Long transScaleLocked;
    @Comment("数据类型格式化")
    private Long transDataTypeFormat;

    public SourceColumnKey sourceColumnKey() {
        return SourceColumnKey.builder().catalog(this.catalog).database(this.database).table(this.table).column(this.column).build();
    }
}