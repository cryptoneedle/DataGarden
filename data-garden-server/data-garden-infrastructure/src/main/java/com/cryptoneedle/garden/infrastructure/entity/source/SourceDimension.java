package com.cryptoneedle.garden.infrastructure.entity.source;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.source.SourceDimensionColumnKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
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
@Comment("数据源-目录")
public class SourceDimension extends BaseEntity {

    @EmbeddedId
    private SourceDimensionColumnKey id;

    @Comment("排序")
    private Long sort;

    @Comment("启用")
    private Boolean enabled;
}