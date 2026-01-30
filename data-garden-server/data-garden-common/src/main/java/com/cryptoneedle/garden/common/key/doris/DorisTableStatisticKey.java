package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@Schema(description = "Doris数据库-表统计-主键")
public class DorisTableStatisticKey implements Serializable {
    
    @Column(length = 64)
    @Comment("数据库")
    @Schema(description = "数据库")
    private String databaseName;
    
    @Column(length = 64)
    @Comment("表")
    @Schema(description = "表")
    private String tableName;
    
    @Comment("统计时间")
    @Schema(description = "统计时间")
    private LocalDateTime statisticDt;
}