package com.cryptoneedle.garden.common.key.config;

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
 * <p>description: 配置-隧道配置-主键 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Embeddable
@Schema(description = "配置-隧道配置-主键")
public class ConfigSshKey implements Serializable {
    
    @Comment("主机名称")
    @Schema(description = "主机名称")
    private String host;
}