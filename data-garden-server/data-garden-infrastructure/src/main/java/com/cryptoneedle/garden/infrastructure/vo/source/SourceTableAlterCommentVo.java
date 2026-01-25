package com.cryptoneedle.garden.infrastructure.vo.source;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-24
 */
@Data
@Schema(description = "数据源-表注释-修改VO")
public class SourceTableAlterCommentVo {
    
    @Schema(description = "表名称")
    private String tableName;
    
    @Schema(description = "注释")
    private String comment;
}