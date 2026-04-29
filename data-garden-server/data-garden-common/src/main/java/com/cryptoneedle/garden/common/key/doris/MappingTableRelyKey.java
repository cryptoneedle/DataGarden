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
@Schema(description = "Doris数据库-表映射-主键")
public class MappingTableRelyKey implements Serializable {
    
    @Column(length = 64)
    @Comment("原数据库")
    @Schema(description = "原数据库")
    private String sourceDatabaseName;
    
    @Column(length = 64)
    @Comment("原表")
    @Schema(description = "原表")
    private String sourceTableName;
    
    @Column(length = 64)
    @Comment("映射数据库")
    @Schema(description = "映射数据库")
    private String mappingDatabaseName;
    
    @Column(length = 64)
    @Comment("映射表")
    @Schema(description = "映射表")
    private String mappingTableName;
    
}