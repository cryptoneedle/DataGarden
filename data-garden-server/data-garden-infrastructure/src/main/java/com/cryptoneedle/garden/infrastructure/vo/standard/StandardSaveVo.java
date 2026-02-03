package com.cryptoneedle.garden.infrastructure.vo.standard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>description: 配置-数仓标准层-新增VO </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-17
 */
@Data
@Schema(description = "配置-数仓标准层-新增VO")
public class StandardSaveVo {
    
    @Schema(description = "标准名称")
    private String name;
    
    @Schema(description = "标准编码")
    private String code;
    
    @Schema(description = "标准列信息")
    private List<Column> columns;
    
    @Data
    public static class Column {
        
        @Schema(description = "列名称")
        private String name;
        
        @Schema(description = "说明")
        private String comment;
        
        @Schema(description = "排序")
        private Long sort;
    }
}