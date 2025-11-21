package com.cryptoneedle.garden.common.key.doris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * <p>description: Doris数据库-字段-主键 </p>
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
@Schema(description = "Doris数据库-字段-主键")
public class DorisColumnKey implements Serializable {

    @Column(length = 64)
    @Comment("数据库")
    @Schema(description = "数据库")
    private String databaseName;

    @Column(length = 64)
    @Comment("表")
    @Schema(description = "表")
    private String tableName;

    @Column(length = 64)
    @Comment("字段")
    @Schema(description = "字段")
    private String columnName;

    public DorisDatabaseKey dorisDatabaseKey() {
        return DorisDatabaseKey.builder().databaseName(this.databaseName).build();
    }

    public DorisTableKey dorisTableKey() {
        return DorisTableKey.builder().databaseName(this.databaseName).tableName(this.tableName).build();
    }
}