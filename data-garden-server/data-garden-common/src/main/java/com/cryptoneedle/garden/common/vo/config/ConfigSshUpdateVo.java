package com.cryptoneedle.garden.common.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>description: 配置-隧道配置-修改VO </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-11
 */
@Data
@Schema(description = "配置-隧道配置-修改VO")
public class ConfigSshUpdateVo {
    
    @NotBlank(message = "主机不能为空")
    @Schema(description = "主机")
    private String host;
    
    @Schema(description = "端口")
    private Integer port;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "密码")
    private String password;
}