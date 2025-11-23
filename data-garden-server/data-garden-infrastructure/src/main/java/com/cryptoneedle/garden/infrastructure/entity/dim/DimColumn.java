package com.cryptoneedle.garden.infrastructure.entity.dim;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: 数据维度层(DIM)-字段-实体 </p>
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
@Table(name = "warehouse_dim_column")
@Comment("数据维度层(DIM)-字段")
public class DimColumn extends BaseEntity {

    @EmbeddedId
    private DorisColumnKey id;

    @Comment("排序")
    private Long sort;
}