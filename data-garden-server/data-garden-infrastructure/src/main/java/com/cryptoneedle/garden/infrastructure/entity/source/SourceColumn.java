package com.cryptoneedle.garden.infrastructure.entity.source;

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
@Builder
@Accessors(chain = true)
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "source_column")
@Comment("数据源-字段")
public class SourceColumn extends BaseEntity {
    
    @EmbeddedId
    private SourceColumnKey id;
    
    @Column(columnDefinition = "TEXT")
    @Comment("字段说明")
    private String comment;
    
    @Comment("Doris目录")
    private String dorisCatalog;
    @Comment("系统编码 (会覆盖目录、数据库、表配置)")
    private String systemCode;
    
    @Enumerated(EnumType.STRING)
    @Comment("字段类型")
    private SourceColumnType columnType;
    @Comment("表")
    private String transTableName;
    @Comment("字段")
    private String transColumnName;
    @Column(columnDefinition = "TEXT")
    @Comment("字段说明")
    private String transComment;
    @Comment("排序")
    private Long sort;
    @Comment("排序")
    private Integer transSort;
    
    @Comment("数据类型格式化")
    private Long dataTypeFormat;
    @Comment("数据类型格式化")
    private Long transDataTypeFormat;
    @Comment("数据类型")
    private String dataType;
    @Comment("数据类型")
    private String transDataType;
    @Comment("长度")
    private Long length;
    @Comment("长度")
    private Long transLength;
    @Comment("精度")
    private Long precision;
    @Comment("精度")
    private Long transPrecision;
    @Comment("标度")
    private Long scale;
    @Comment("标度")
    private Long transScale;
    @Comment("非空")
    private Boolean notNull = false;
    @Comment("非空")
    private Boolean transNotNull;
    
    @Comment("数据量")
    private Long rowNum;
    @Comment("采样数据量")
    private Long sampleNum;
    @Column(precision = 10, scale = 2)
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
    @Column(name = "storage_mb", precision = 30, scale = 20)
    @Comment("预估占用空间(单位：MBytes)")
    private BigDecimal storageMegaBytes;
    @Comment("预估占用空间(格式化)")
    private String storageFormat;
    @Comment("统计时间")
    private LocalDateTime statisticDt;
    
    @Comment("锁定字段")
    private Boolean transColumnLocked = false;
    @Comment("锁定说明")
    private Boolean transCommentLocked = false;
    @Comment("锁定数据类型")
    private String transDataTypeLocked;
    
    @Comment("启用")
    private Boolean enabled = false;
}