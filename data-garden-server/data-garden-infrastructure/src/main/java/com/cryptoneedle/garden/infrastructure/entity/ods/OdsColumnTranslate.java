package com.cryptoneedle.garden.infrastructure.entity.ods;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.doris.OdsColumnTranslateKey;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: 数据映射层(MAPPING)-表-实体 </p>
 *
 * @author CryptoNeedle
 * @date 2026-02-03
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
@Table(name = "deal_ods_column_translate")
@Comment("数据映射层(MAPPING)-字段翻译")
public class OdsColumnTranslate extends BaseEntity {
    
    @EmbeddedId
    private OdsColumnTranslateKey id;
    
    @Column(length = 64)
    @Comment("字段翻译")
    @Schema(description = "字段翻译")
    private String translate;
}