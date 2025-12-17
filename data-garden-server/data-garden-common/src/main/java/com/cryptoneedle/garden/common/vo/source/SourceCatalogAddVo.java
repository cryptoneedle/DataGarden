package com.cryptoneedle.garden.common.vo.source;

import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>description: 配置-数据源目录-新增VO </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-17
 */
@Data
@Schema(description = "配置-数据源目录-新增VO")
public class SourceCatalogAddVo {
    
    @Schema(description = "目录")
    private String catalogName;
    
    @Schema(description = "Doris目录")
    private String dorisCatalogName;
    @Schema(description = "默认系统编码")
    private String systemCode;
    @Schema(description = "默认采集频率")
    private SourceCollectFrequencyType collectFrequency;
    @Schema(description = "默认采集频率对应的时间点 (每天为具体小时开始，每小时为具体分钟开始，每分钟为具体分开始)")
    private Integer collectTimePoint;
    
    @Schema(description = "主机")
    private String host;
    @Schema(description = "端口")
    private Integer port;
    @Schema(description = "数据库类型标识")
    private String databaseType;
    @Schema(description = "连接类型")
    private String connectType;
    @Schema(description = "路径/数据库名")
    private String route;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "SSH主机")
    private String sshHost;
}