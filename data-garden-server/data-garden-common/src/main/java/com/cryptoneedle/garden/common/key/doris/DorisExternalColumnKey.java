package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * <p>description: Doris外部数据库-字段-主键 </p>
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
@Schema(description = "Doris外部数据库-字段-主键")
public class DorisExternalColumnKey implements Serializable {
    
    @Comment("目录")
    @Schema(description = "目录")
    private String catalogName;
    
    @Comment("数据库")
    @Schema(description = "数据库")
    private String databaseName;
    
    @Comment("表")
    @Schema(description = "表")
    private String tableName;
    
    @Comment("字段")
    @Schema(description = "字段")
    private String columnName;
    
    public DorisCatalogKey dorisCatalogKey() {
        return DorisCatalogKey.builder().catalogName(this.catalogName).build();
    }
    
    public DorisExternalDatabaseKey dorisExternalDatabaseKey() {
        return DorisExternalDatabaseKey.builder().catalogName(this.catalogName).databaseName(this.databaseName).build();
    }
    
    public DorisExternalTableKey dorisExternalTableKey() {
        return DorisExternalTableKey.builder()
                                    .catalogName(this.catalogName)
                                    .databaseName(this.databaseName)
                                    .tableName(this.tableName)
                                    .build();
    }
}