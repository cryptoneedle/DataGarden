package com.cryptoneedle.garden.infrastructure.entity.ads;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: 数据应用层(ADS)-表-实体 </p>
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
@Table(name = "warehouse_ads_table")
@Comment("数据应用层(ADS)-表")
public class AdsTable extends BaseEntity {

    @EmbeddedId
    private DorisTableKey id;
}