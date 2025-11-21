package com.cryptoneedle.garden.common.key.source;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * <p>description: 数据源-字段-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Embeddable
@Schema(description = "数据源-字段-主键")
public class SourceColumnKey implements Serializable {

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

    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalogName(this.catalogName).build();
    }

    public SourceDatabaseKey sourceDatabaseKey() {
        return SourceDatabaseKey.builder().catalogName(this.catalogName).databaseName(this.databaseName).build();
    }

    public SourceTableKey sourceTableKey() {
        return SourceTableKey.builder().catalogName(this.catalogName).databaseName(this.databaseName).tableName(this.tableName).build();
    }
}