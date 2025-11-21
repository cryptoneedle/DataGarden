package com.cryptoneedle.garden.infrastructure.entity.doris;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.doris.DorisDatabaseKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: DORIS-数据库-实体 </p>
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
@Table(name = "doris_database")
@Comment("DORIS-数据库")
public class DorisDatabase extends BaseEntity {

    @EmbeddedId
    private DorisDatabaseKey id;

    @Comment("表数量")
    private Integer tableNum;
    @Comment("数据源关联表数量")
    private Integer sourceTableNum;

    @Comment("唯一模型表数量")
    private Integer uniqueNum;
    @Comment("明细模型表数量")
    private Integer duplicateNum;
    @Comment("明细模型表数量")
    private Integer aggregateNum;

    @Comment("排序")
    private Integer sort;
}