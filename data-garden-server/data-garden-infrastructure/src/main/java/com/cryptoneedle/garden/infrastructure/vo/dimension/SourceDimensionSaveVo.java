package com.cryptoneedle.garden.infrastructure.vo.dimension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>description: 配置-数据源目录-新增VO </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-17
 */
@Data
@Schema(description = "配置-数据源目录-新增VO")
public class SourceDimensionSaveVo {
    
    @Schema(description = "维度名称")
    private String dimensionName;
    
    @Schema(description = "列名称")
    private List<String> columnNames;
}