package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * <p>description: Doris数据库-表统计-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode
@Embeddable
@Schema(description = "Doris数据库-表血缘-主键")
public class DorisLineageTableKey implements Serializable {
    
    @Column(length = 64)
    @Comment("目标数据库")
    @Schema(description = "目标数据库")
    private String toDatabaseName;
    
    @Column(length = 64)
    @Comment("目标表")
    @Schema(description = "目标表")
    private String toTableName;
    
    @Column(length = 64)
    @Comment("来源数据库")
    @Schema(description = "来源数据库")
    private String fromDatabaseName;
    
    @Column(length = 64)
    @Comment("来源表")
    @Schema(description = "来源表")
    private String fromTableName;
}