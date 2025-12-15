package com.cryptoneedle.garden.common.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>description: 配置-隧道配置-新增VO </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-11
 */
@Data
@Schema(description = "配置-隧道配置-新增VO")
public class ConfigSshAddVo {
    
    @NotBlank(message = "主机不能为空")
    @Schema(description = "主机")
    private String host;
    
    @Schema(description = "端口")
    private int port = 22;
    
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}