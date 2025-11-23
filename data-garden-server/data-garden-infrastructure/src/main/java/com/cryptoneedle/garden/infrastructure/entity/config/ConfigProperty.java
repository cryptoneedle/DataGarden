package com.cryptoneedle.garden.infrastructure.entity.config;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.ConfigPropertyType;
import com.cryptoneedle.garden.common.key.config.ConfigPropertyKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: 配置-属性配置-实体 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-23
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
@Table(name = "config_property")
@Comment("配置-属性配置-实体")
public class ConfigProperty extends BaseEntity {

    @EmbeddedId
    private ConfigPropertyKey id;

    @Comment("属性值")
    private String value;

    @Comment("说明")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Comment("配置方式")
    private ConfigPropertyType type;
}