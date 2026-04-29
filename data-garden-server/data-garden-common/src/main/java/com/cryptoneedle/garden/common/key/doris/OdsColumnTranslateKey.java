package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * <p>description: Doris数据库-表-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode
@Embeddable
@Schema(description = "Doris数据库-字段翻译-主键")
public class OdsColumnTranslateKey implements Serializable {
    
    @Column(length = 64)
    @Comment("数据库")
    @Schema(description = "原数据库")
    private String databaseName;
    
    @Column(length = 64)
    @Comment("表")
    @Schema(description = "表")
    private String tableName;
    
    @Column(length = 64)
    @Comment("列")
    @Schema(description = "列")
    private String columnName;
    
    @Column(length = 64)
    @Comment("字段值")
    @Schema(description = "字段值")
    private String value;
}