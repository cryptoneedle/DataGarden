package com.cryptoneedle.garden.infrastructure.entity.doris;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: DORIS-字段-实体 </p>
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
@Table(name = "doris_column")
@Comment("DORIS-字段")
public class DorisColumn extends BaseEntity {
    
    @EmbeddedId
    private DorisColumnKey id;
    
    @Column(length = 2048)
    @Comment("详情")
    private String comment;
    
    @Comment("排序")
    private Long sort;
    
    // todo Enums DorisColumnType
    @Column(length = 3)
    @Comment("如果是 UNI，则表示当前字段是 Unique Key 字段")
    private String columnType;
    @Column(length = 32)
    @Comment("字段类型")
    private String dataTypeFormat;
    @Column(length = 64)
    @Comment("数据类型")
    private String dataType;
    @Comment("字段宽度")
    private Long length;
    @Column(length = 1024)
    @Comment("精度")
    private Long precision;
    @Comment("DATETIME类型的精度")
    private Long datetimePrecision;
    @Comment("标度")
    private Long scale;
    @Comment("数值类型的小数位数")
    private Long decimalDigits;
    @Column(length = 27)
    @Comment("字段的一些额外信息。包括展示是否为自增字段，是否为 Generated 字段等")
    private String extra;
    @Comment("默认值")
    private String defaultValue;
    @Comment("字符类型允许的最大字符数")
    private Long charMaxLength;
    @Comment("字符类型允许的最大字节数")
    private Long byteMaxLength;
    
    @Comment("排序")
    private Integer databaseSort;
}