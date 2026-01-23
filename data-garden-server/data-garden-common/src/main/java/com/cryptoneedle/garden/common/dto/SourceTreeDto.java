package com.cryptoneedle.garden.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>description: 数据源树Dto </p>
 *
 * @author CryptoNeedle
 * @date 2025-02-18
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据源树Dto")
public class SourceTreeDto {

    @Schema(description = "当前节点ID")
    private String id;

    @Schema(description = "父节点ID")
    private String parentId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "节点类型")
    private String nodeType;

    @Schema(description = "数据库类型")
    private String databaseType;

    @Schema(description = "是否开启")
    private Boolean enabled;

    @Schema(description = "激活维度名称")
    private String activeDimensionName;
}