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
 * <p>description: 数据源-数据库-主键 </p>
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
@Comment("数据源-数据库-主键")
@Schema(description = "数据源-数据库-主键")
public class SourceDatabaseKey implements Serializable {

    @Comment("目录")
    @Schema(description = "目录")
    private String catalogName;

    @Comment("数据库")
    @Schema(description = "数据库")
    private String databaseName;

    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalogName(this.catalogName).build();
    }
}