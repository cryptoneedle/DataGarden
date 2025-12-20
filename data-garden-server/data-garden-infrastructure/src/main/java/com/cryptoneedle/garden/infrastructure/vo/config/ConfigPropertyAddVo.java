package com.cryptoneedle.garden.infrastructure.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>description: 配置-属性配置-新增VO </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-11
 */
@Data
@Schema(description = "配置-属性配置-新增VO")
public class ConfigPropertyAddVo {
    
    @NotBlank(message = "属性名称不能为空")
    @Schema(description = "属性名称")
    private String propertyName;
    
    @NotBlank(message = "属性值不能为空")
    @Schema(description = "属性值")
    private String value;
    
    @Schema(description = "说明")
    private String comment;
}