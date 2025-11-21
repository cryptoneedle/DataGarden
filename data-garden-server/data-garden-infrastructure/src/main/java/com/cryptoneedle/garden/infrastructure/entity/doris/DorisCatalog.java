package com.cryptoneedle.garden.infrastructure.entity.doris;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.DorisCatalogType;
import com.cryptoneedle.garden.common.key.doris.DorisCatalogKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: DORIS-目录-实体 </p>
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
@Table(name = "doris_catalog")
@Comment("DORIS-目录")
public class DorisCatalog extends BaseEntity {

    @EmbeddedId
    private DorisCatalogKey id;

    @Comment("目录类型")
    private DorisCatalogType catalogType;
    @Column(length = 2048)
    @Comment("说明")
    private String comment;
    @Comment("数据源目录")
    private String sourceCatalog;

    @Comment("创建时间(需采用字符类型)")
    private String createDt;
    @Comment("更新时间(需采用字符类型)")
    private String updateDt;
}