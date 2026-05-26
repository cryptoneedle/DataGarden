package com.cryptoneedle.garden.infrastructure.entity.doris;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.doris.DorisLineageTableKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableStatisticKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: DORIS-表-实体 </p>
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
@Table(name = "doris_lineage_table")
@Comment("DORIS-血缘-表")
public class DorisLineageTable extends BaseEntity {
    
    @EmbeddedId
    private DorisLineageTableKey id;
}