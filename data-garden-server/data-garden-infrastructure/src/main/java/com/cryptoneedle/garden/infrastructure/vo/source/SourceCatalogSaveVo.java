package com.cryptoneedle.garden.infrastructure.vo.source;

import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.enums.ValidType;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>description: 配置-数据源目录-新增VO </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-17
 */
@Data
@Schema(description = "配置-数据源目录-新增VO")
public class SourceCatalogSaveVo {
    
    @NotBlank(message = "目录不能为空")
    @Schema(description = "目录")
    private String catalogName;
    
    @NotBlank(message = "Doris目录不能为空")
    @Schema(description = "Doris目录")
    private String dorisCatalogName;
    @NotBlank(message = "默认系统编码不能为空")
    @Schema(description = "默认系统编码")
    private String systemCode;
    @NotNull(message = "默认采集频率不能为空")
    @Schema(description = "默认采集频率")
    private SourceCollectFrequencyType collectFrequency;
    @NotNull(message = "采集频率时间点不能为空")
    @Schema(description = "默认采集频率对应的时间点 (每天为具体小时开始，每小时为具体分钟开始，每分钟为具体分开始)")
    private Integer collectTimePoint;
    
    @NotBlank(message = "主机不能为空")
    @Schema(description = "主机")
    private String host;
    @NotNull(message = "端口不能为空")
    @Schema(description = "端口")
    private Integer port;
    @NotBlank(message = "数据库类型不能为空")
    @Schema(description = "数据库类型标识")
    private String databaseType;
    @NotBlank(message = "连接类型不能为空")
    @Schema(description = "连接类型")
    private String connectType;
    @Schema(description = "路径")
    private String route;
    @NotBlank(message = "用户名不能为空", groups = {ValidType.Add.class, ValidType.TestAdd.class})
    @Schema(description = "用户名")
    private String username;
    @NotBlank(message = "密码不能为空", groups = {ValidType.Add.class, ValidType.TestAdd.class})
    @Schema(description = "密码")
    private String password;
    @Schema(description = "SSH主机")
    private String sshHost;
    
    public SourceCatalogKey sourceCatalogKey() {
        return SourceCatalogKey.builder().catalogName(this.catalogName).build();
    }
    
    public SourceCatalog sourceCatalog() {
        return SourceCatalog.builder()
                            .id(sourceCatalogKey())
                            .dorisCatalogName(this.dorisCatalogName)
                            .systemCode(this.systemCode)
                            .collectFrequency(this.collectFrequency)
                            .collectTimePoint(this.collectTimePoint)
                            .host(this.host)
                            .port(this.port)
                            .databaseType(this.databaseType)
                            .connectType(this.connectType)
                            .route(this.route)
                            .username(this.username)
                            .password(this.password)
                            // 初始化
                            .url(null)
                            .version(null)
                            .serverConnected(false)
                            .jdbcConnected(false)
                            .dorisConnected(false)
                            .serverConnectedDt(null)
                            .jdbcConnectedDt(null)
                            .dorisConnectedDt(null)
                            .enabled(false)
                            .configSsh(null)
                            .build();
    }
    
    public SourceCatalog sourceCatalog(SourceCatalog sourceCatalog) {
        return SourceCatalog.builder()
                            .id(sourceCatalogKey())
                            .dorisCatalogName(this.dorisCatalogName)
                            .systemCode(this.systemCode)
                            .collectFrequency(this.collectFrequency)
                            .collectTimePoint(this.collectTimePoint)
                            .host(this.host)
                            .port(this.port)
                            .databaseType(this.databaseType)
                            .connectType(this.connectType)
                            .route(this.route)
                            .username(this.username)
                            .password(this.password)
                            // 初始化
                            .url(null)
                            .version(sourceCatalog.getVersion())
                            .serverConnected(sourceCatalog.getServerConnected())
                            .jdbcConnected(sourceCatalog.getJdbcConnected())
                            .dorisConnected(sourceCatalog.getDorisConnected())
                            .serverConnectedDt(sourceCatalog.getServerConnectedDt())
                            .jdbcConnectedDt(sourceCatalog.getJdbcConnectedDt())
                            .dorisConnectedDt(sourceCatalog.getDorisConnectedDt())
                            .enabled(sourceCatalog.getEnabled())
                            .configSsh(null)
                            .build();
    }
}