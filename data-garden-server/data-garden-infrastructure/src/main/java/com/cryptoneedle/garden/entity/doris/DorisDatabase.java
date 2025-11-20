package com.cryptoneedle.garden.entity.doris;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.key.doris.DorisDatabaseKey;
import jakarta.persistence.*;
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
@ToString
@Builder
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "doris_database")
@IdClass(DorisDatabaseKey.class)
@Comment("DORIS-数据库")
public class DorisDatabase extends BaseEntity {

    @Id
    @Column(length = 64)
    @Comment("数据库")
    private String database;

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
}