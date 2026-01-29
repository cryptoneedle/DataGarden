package com.cryptoneedle.garden.common.key.source;

import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * <p>description: 数据源-维度字段-主键 </p>
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
@Schema(description = "数据源-维度字段-主键")
public class SourceDimensionColumnKey implements Serializable {
    
    @Comment("目录")
    @Schema(description = "目录")
    private String catalogName;
    
    @Comment("数据库")
    @Schema(description = "数据库")
    private String databaseName;
    
    @Comment("表")
    @Schema(description = "表")
    private String tableName;
    
    @Enumerated(EnumType.STRING)
    @Comment("维度类型")
    @Schema(description = "维度类型")
    private SourceDimensionType dimensionType;
    
    @Comment("维度")
    @Schema(description = "维度")
    private String dimensionName;
    
    @Comment("字段")
    @Schema(description = "字段")
    private String columnName;
    
    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalogName(this.catalogName).build();
    }
    
    public SourceDatabaseKey sourceDatabaseKey() {
        return SourceDatabaseKey.builder().catalogName(this.catalogName).databaseName(this.databaseName).build();
    }
    
    public SourceTableKey sourceTableKey() {
        return SourceTableKey.builder()
                             .catalogName(this.catalogName)
                             .databaseName(this.databaseName)
                             .tableName(this.tableName)
                             .build();
    }
    
    public SourceDimensionKey sourceDimensionKey() {
        return SourceDimensionKey.builder()
                                 .catalogName(this.catalogName)
                                 .databaseName(this.databaseName)
                                 .tableName(this.tableName)
                                 .dimensionType(this.dimensionType)
                                 .dimensionName(this.dimensionName)
                                 .build();
    }
    
    public SourceColumnKey sourceColumnKey() {
        return SourceColumnKey.builder()
                              .catalogName(this.catalogName)
                              .databaseName(this.databaseName)
                              .tableName(this.tableName)
                              .columnName(this.columnName)
                              .build();
    }
    
    public String commonDimensionNameTable() {
        return this.catalogName + "_" + this.databaseName + "_" + this.tableName + "_" + this.dimensionName;
    }
}