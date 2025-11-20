package com.cryptoneedle.garden.entity.source;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import com.cryptoneedle.garden.common.key.source.SourceDimensionColumnKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: 数据源-维度-实体 </p>
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
@Table(name = "source_dimension")
@IdClass(SourceDimensionColumnKey.class)
@Comment("数据源-目录")
public class SourceDimension extends BaseEntity {

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
    @Enumerated(EnumType.STRING)
    @Comment("维度来源")
    private SourceDimensionType dimensionType;
    @Id
    @Comment("维度")
    private String dimension;
    @Id
    @Comment("字段")
    private String column;

    @Comment("排序")
    private Long sort;

    @Comment("启用")
    private Boolean enabled;

    public SourceDimensionColumnKey sourceDimensionColumnKey() {
        return SourceDimensionColumnKey.builder().catalog(this.catalog).database(this.database).table(this.table).dimensionType(this.dimensionType).dimension(this.dimension).column(this.column).build();
    }
}