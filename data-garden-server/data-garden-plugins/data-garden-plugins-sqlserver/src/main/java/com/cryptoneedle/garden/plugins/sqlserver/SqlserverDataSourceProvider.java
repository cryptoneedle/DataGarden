package com.cryptoneedle.garden.plugins.sqlserver;

import com.cryptoneedle.garden.common.constants.CommonConstant;
import com.cryptoneedle.garden.common.enums.DorisDataType;
import com.cryptoneedle.garden.common.enums.SourceTimeType;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceColumn;
import com.cryptoneedle.garden.spi.DataSourceProvider;
import com.cryptoneedle.garden.spi.SshUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.List;

/**
 * <p>description: Oracle数据源元数据提供者实现 </p>
 * <p>
 * 提供Oracle数据库的元数据查询SQL定义，包括：
 * - 数据库版本查询SQL
 * - Schema列表查询SQL
 * - 表和视图列表查询SQL
 * - 列信息查询SQL
 * - 统计信息查询SQL
 * </p>
 * <p>
 * 具体的查询执行由SPI框架的SourceMetadataExecutor统一实现。
 * </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-29
 */
public class SqlserverDataSourceProvider implements DataSourceProvider {
    
    private final String JDBC_URL_TEMPLATE = "jdbc:jtds:sqlserver://%s:%s;databaseName=%s;encrypt=false;";
    
    private final String FILTER_SYSTEM_DATABASE_CONDITION = """
            AND s.name NOT IN (
              'master', 'tempdb', 'model', 'msdb', 'distribution', 'reportserver', 'reportservertempdb', 'ReportServerTempDB', 'ReportServer', 'SSISDB'
            )""";
    
    @Override
    public String databaseType() {
        return "SQLSERVER";
    }
    
    @Override
    public String buildJdbcUrl(SourceCatalog catalog) {
        if (catalog.getConfigSsh() != null) {
            Integer forwardPort = SshUtil.getForwardPort(catalog);
            return JDBC_URL_TEMPLATE.formatted(CommonConstant.LOCAL_HOST, forwardPort, catalog.getRoute());
        } else {
            return JDBC_URL_TEMPLATE.formatted(catalog.getHost(), catalog.getPort(), catalog.getRoute());
        }
    }
    
    @Override
    public String databaseSql(String databaseName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND s.name = '%s'".formatted(databaseName) : "";
        return """
                SELECT s.name                                                                                         AS "databaseName"        -- 数据库
                     , COUNT(*)                                                                                       AS "totalNum"            -- 总数(表数量+视图数量+物化视图数量)
                     , SUM(CASE WHEN o.type = 'U' THEN 1 ELSE 0 END)                                                  AS "tableNum"            -- 表数量
                     , SUM(CASE
                               WHEN o.type = 'V' AND OBJECTPROPERTY(o.object_id, 'IsIndexed') = 0 THEN 1
                               ELSE 0 END)                                                                            AS "viewNum"             -- 视图数量
                     , SUM(CASE
                               WHEN o.type = 'V' AND OBJECTPROPERTY(o.object_id, 'IsIndexed') = 1 THEN 1
                               ELSE 0 END)                                                                            AS "materializedViewNum" -- 物化视图数量
                FROM sys.objects o
                         INNER JOIN sys.schemas s ON o.schema_id = s.schema_id
                WHERE o.type IN ('U', 'V')
                   %s
                   %s
                 GROUP BY s.name
                 ORDER BY s.name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition);
    }
    
    @Override
    public String tableSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND s.name = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.name = '%s'".formatted(tableName) : "";
        return """
                SELECT s.name                          AS "databaseName" -- 数据库
                     , t.name                          AS "tableName"    -- 表
                     , CAST(ep.value AS NVARCHAR(MAX)) AS "comment"      -- 表说明
                     , 'TABLE'                         AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , p.row_count                     AS "rowNum"       -- 数据量
                     , CASE
                           WHEN p.row_count = 0 THEN 0
                           WHEN p.row_count > 0 THEN (p.data_pages * 8.0 * 1024) / p.row_count
                           ELSE 0 END                  AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , GETDATE()                       AS "statisticDt"  -- 统计时间
                FROM sys.tables t
                         INNER JOIN sys.schemas s ON t.schema_id = s.schema_id
                         LEFT JOIN sys.extended_properties ep
                                   ON t.object_id = ep.major_id AND ep.minor_id = 0 AND ep.name = 'MS_Description' AND ep.class = 1
                         LEFT JOIN (SELECT p.object_id, SUM(p.rows) AS row_count, SUM(au.data_pages) AS data_pages
                                    FROM sys.partitions p
                                             INNER JOIN sys.allocation_units au ON p.partition_id = au.container_id
                                    WHERE p.index_id IN (0, 1) -- 0:堆表, 1:聚集索引（即表基础数据）
                                    GROUP BY p.object_id) p ON t.object_id = p.object_id
                WHERE 1 = 1
                  %s
                  %s
                  %s
                ORDER BY s.name, t.name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String viewSql(String databaseName, String viewName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND s.name = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(viewName) ? "AND t.name = '%s'".formatted(viewName) : "";
        return """
                SELECT s.name                          AS "databaseName" -- 数据库
                     , t.name                          AS "tableName"    -- 表
                     , CAST(ep.value AS NVARCHAR(MAX)) AS "comment"      -- 表说明
                     , 'VIEW'                          AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , p.row_count                     AS "rowNum"       -- 数据量
                     , CASE
                           WHEN p.row_count = 0 THEN 0
                           WHEN p.row_count > 0 THEN (p.data_pages * 8.0 * 1024) / p.row_count
                           ELSE NULL END               AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , GETDATE()                       AS "statisticDt"  -- 统计时间
                FROM sys.views t
                         INNER JOIN sys.schemas s ON t.schema_id = s.schema_id
                         LEFT JOIN sys.extended_properties ep
                                   ON t.object_id = ep.major_id AND ep.minor_id = 0 AND ep.name = 'MS_Description' AND ep.class = 1
                         LEFT JOIN (SELECT p.object_id, SUM(p.rows) AS row_count, SUM(au.data_pages) AS data_pages
                                    FROM sys.partitions p
                                             INNER JOIN sys.allocation_units au ON p.partition_id = au.container_id
                                    WHERE p.index_id = 1 -- 聚集索引（物化视图必有）
                                    GROUP BY p.object_id) p ON t.object_id = p.object_id
                WHERE OBJECTPROPERTY(t.object_id, 'IsIndexed') = 0
                   %s
                   %s
                   %s
                ORDER BY s.name, t.name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String materializedViewSql(String databaseName, String viewName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND s.name = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(viewName) ? "AND t.name = '%s'".formatted(viewName) : "";
        return """
                SELECT s.name                          AS "databaseName" -- 数据库
                     , t.name                          AS "tableName"    -- 表
                     , CAST(ep.value AS NVARCHAR(MAX)) AS "comment"      -- 表说明
                     , 'MATERIALIZED_VIEW'             AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , p.row_count                     AS "rowNum"       -- 数据量
                     , CASE
                           WHEN p.row_count = 0 THEN 0
                           WHEN p.row_count > 0 THEN (p.data_pages * 8.0 * 1024) / p.row_count
                           ELSE NULL END               AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , GETDATE()                       AS "statisticDt"  -- 统计时间
                FROM sys.views t
                         INNER JOIN sys.schemas s ON t.schema_id = s.schema_id
                         LEFT JOIN sys.extended_properties ep
                                   ON t.object_id = ep.major_id AND ep.minor_id = 0 AND ep.name = 'MS_Description' AND ep.class = 1
                         LEFT JOIN (SELECT p.object_id, SUM(p.rows) AS row_count, SUM(au.data_pages) AS data_pages
                                    FROM sys.partitions p
                                             INNER JOIN sys.allocation_units au ON p.partition_id = au.container_id
                                    WHERE p.index_id = 1 -- 聚集索引（物化视图必有）
                                    GROUP BY p.object_id) p ON t.object_id = p.object_id
                WHERE OBJECTPROPERTY(t.object_id, 'IsIndexed') = 1
                   %s
                   %s
                   %s
                ORDER BY s.name, t.name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String columnSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND s.name = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.name = '%s'".formatted(tableName) : "";
        return """
                SELECT s.name                                        AS "databaseName"   -- 数据库
                     , t.name                                        AS "tableName"      -- 表
                     , c.name                                        AS "columnName"     -- 字段
                     , CAST(ep.value AS NVARCHAR(MAX))               AS "comment"        -- 字段说明
                     , c.column_id                                   AS "sort"           -- 排序
                     , tp.name                                       AS "dataType"       -- 数据类型
                     , c.max_length                                  AS "length"         -- 长度
                     , c.precision                                   AS "precision"      -- 精度
                     , c.scale                                       AS "scale"          -- 标度
                     , CASE WHEN c.is_nullable = 1 THEN 0 ELSE 1 END AS "notNull"        -- 不可空
                     , NULL                                          AS "sampleNum"      -- 采样数据量
                     , NULL                                          AS "nullNum"        -- 采样空值数据量
                     , NULL                                          AS "distinctNum"    -- 采样基数
                     , NULL                                          AS "density"        -- 采样数据密度
                     , NULL                                          AS "minValue"       -- 采样最小值
                     , NULL                                          AS "maxValue"       -- 采样最大值
                     , NULL                                          AS "avgColumnBytes" -- 字段平均占用空间(单位：Byte)
                     , NULL                                          AS "statisticDt"    -- 统计时间
                FROM sys.columns c
                         JOIN sys.objects t ON c.object_id = t.object_id
                         JOIN sys.schemas s ON t.schema_id = s.schema_id
                         JOIN sys.types tp ON c.user_type_id = tp.user_type_id
                         LEFT JOIN sys.extended_properties ep
                                   ON ep.major_id = c.object_id AND ep.minor_id = c.column_id AND ep.name = 'MS_Description' AND
                                      ep.class = 1
                WHERE 1 = 1
                   %s
                   %s
                   %s
                 ORDER BY s.name, t.name, c.column_id, c.name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String primaryConstraintSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND s.name = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.name = '%s'".formatted(tableName) : "";
        return """
                SELECT s.name               AS "databaseName"  -- 数据库
                     , t.name               AS "tableName"     -- 表
                     , 'PRIMARY_CONSTRAINT' AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , kc.name              AS "dimensionName" -- 维度
                     , c.name               AS "columnName"    -- 字段
                     , ic.key_ordinal       AS "sort"          -- 排序
                FROM sys.key_constraints kc
                         JOIN sys.tables t ON kc.parent_object_id = t.object_id
                         JOIN sys.schemas s ON t.schema_id = s.schema_id
                         JOIN sys.indexes i ON kc.parent_object_id = i.object_id AND kc.unique_index_id = i.index_id
                         JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
                         JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
                WHERE kc.type = 'PK'
                  AND ic.is_included_column = 0
                  %s
                  %s
                  %s
                ORDER BY s.name, t.name, kc.name, ic.key_ordinal""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String uniqueConstraintSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND s.name = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.name = '%s'".formatted(tableName) : "";
        return """
                SELECT s.name               AS "databaseName"  -- 数据库
                     , t.name               AS "tableName"     -- 表
                     , 'UNIQUE_CONSTRAINT'  AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , kc.name              AS "dimensionName" -- 维度
                     , c.name               AS "columnName"    -- 字段
                     , ic.key_ordinal       AS "sort"          -- 排序
                FROM sys.key_constraints kc
                         JOIN sys.tables t ON kc.parent_object_id = t.object_id
                         JOIN sys.schemas s ON t.schema_id = s.schema_id
                         JOIN sys.indexes i ON kc.parent_object_id = i.object_id AND kc.unique_index_id = i.index_id
                         JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
                         JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
                WHERE kc.type = 'UQ'
                  %s
                  %s
                  %s
                ORDER BY s.name, t.name, kc.name, ic.key_ordinal""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String uniqueIndexSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND s.name = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.name = '%s'".formatted(tableName) : "";
        return """
                SELECT s.name         AS "databaseName"  -- 数据库
                     , t.name         AS "tableName"     -- 表
                     , 'UNIQUE_INDEX' AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , i.name         AS "dimensionName" -- 维度
                     , c.name         AS "columnName"    -- 字段
                     , ic.key_ordinal AS "sort"          -- 排序
                FROM sys.indexes i
                         JOIN sys.tables t ON i.object_id = t.object_id
                         JOIN sys.schemas s ON t.schema_id = s.schema_id
                         JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
                         JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
                WHERE i.is_unique = 1
                  AND i.is_primary_key = 0
                  AND i.is_unique_constraint = 0
                  AND ic.is_included_column = 0
                  %s
                  %s
                  %s
                ORDER BY s.name, t.name, i.name, ic.key_ordinal""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public void transform(SourceColumn column) {
        String dataType = column.getDataType();
        Long length = column.getLength();
        Long precision = column.getPrecision();
        Long scale = column.getScale();
        
        DorisDataType dorisDataType = null;
        Long dorisLength = null;
        Long dorisPrecision = null;
        Long dorisScale = null;
        String warn = null;
        String error = null;
        Boolean dorisDataTypeToChar = false;
        
        switch (dataType) {
            case "bit" -> {
                dorisDataType = DorisDataType.BOOLEAN;
            }
            case "tinyint" -> {
                dorisDataType = DorisDataType.SMALLINT;
            }
            case "smallint" -> {
                dorisDataType = DorisDataType.SMALLINT;
            }
            case "int" -> {
                dorisDataType = DorisDataType.INT;
            }
            case "bigint" -> {
                dorisDataType = DorisDataType.BIGINT;
            }
            case "real" -> {
                dorisDataType = DorisDataType.FLOAT;
                dorisPrecision = precision;
                dorisScale = scale;
            }
            case "float" -> {
                dorisDataType = DorisDataType.DOUBLE;
                dorisPrecision = precision;
                dorisScale = scale;
            }
            case "money" -> {
                dorisDataType = DorisDataType.DECIMAL;
                dorisPrecision = 19L;
                dorisScale = 4L;
            }
            case "smallmoney" -> {
                dorisDataType = DorisDataType.DECIMAL;
                dorisPrecision = 10L;
                dorisScale = 4L;
            }
            case "date" -> {
                dorisDataType = DorisDataType.DATE;
            }
            case "datetime" -> {
                dorisDataType = DorisDataType.DATETIME;
                dorisScale = 3L;
            }
            case "datetime2" -> {
                dorisDataType = DorisDataType.DATETIME;
                if (precision <= 6) {
                    dorisScale = precision;
                } else {
                    dorisScale = 6L;
                }
            }
            case "smalldatetime" -> {
                dorisDataType = DorisDataType.DATETIME;
            }
            case "char" -> {
                dorisDataType = DorisDataType.VARCHAR;
                if (length == -1) {
                    dorisLength = 65533L;
                } else {
                    dorisLength = length;
                }
            }
            case "varchar" -> {
                dorisDataType = DorisDataType.VARCHAR;
                if (length == -1) {
                    dorisLength = 65533L;
                } else {
                    dorisLength = length;
                }
            }
            case "text" -> {
                dorisDataType = DorisDataType.STRING;
                dorisLength = length;
            }
            case "nchar" -> {
                dorisDataType = DorisDataType.VARCHAR;
                if (length == -1) {
                    dorisLength = 65533L;
                } else {
                    dorisLength = length;
                }
            }
            case "nvarchar" -> {
                dorisDataType = DorisDataType.VARCHAR;
                if (length == -1) {
                    dorisLength = 65533L;
                } else {
                    dorisLength = length;
                }
            }
            case "ntext" -> {
                dorisDataType = DorisDataType.STRING;
            }
            case "time" -> {
                dorisDataType = DorisDataType.STRING;
            }
            case "datetimeoffset" -> {
                dorisDataType = DorisDataType.STRING;
            }
            case "timestamp" -> {
                dorisDataType = DorisDataType.STRING;
            }
            case "image" -> {
                // todo 官方建议 varbinary
                dorisDataType = DorisDataType.STRING;
            }
            case "binary" -> {
                // todo 官方建议 varbinary
                dorisDataType = DorisDataType.BOOLEAN;
            }
            case "varbinary" -> {
                // todo 官方建议 varbinary
                dorisDataType = DorisDataType.BOOLEAN;
            }
            case "decimal" -> {
                dorisDataType = DorisDataType.DECIMAL;
                dorisPrecision = precision;
                dorisScale = scale;
            }
            case "numeric" -> {
                if (precision == null) {
                    dorisDataType = DorisDataType.LARGEINT;
                    warn = "未指定p和s的number(p,s)类型: number(" + precision + "," + scale + "), length=" + length;
                } else if (scale == null || scale == 0) {
                    if (precision == 1) {
                        // 特殊处理number(1)为字符类型，Seatunnel会识别为布尔值，但scale的影响未知。在这里需添加标记，以便在后面的脚本生成过程中使用to_char()转换
                        dorisDataType = DorisDataType.TINYINT;
                        dorisDataTypeToChar = true;
                    } else if (precision < 3) {
                        dorisDataType = DorisDataType.TINYINT;
                    } else if (precision < 5) {
                        dorisDataType = DorisDataType.SMALLINT;
                    } else if (precision < 10) {
                        dorisDataType = DorisDataType.INT;
                    } else if (precision < 19) {
                        dorisDataType = DorisDataType.BIGINT;
                    } else {
                        dorisDataType = DorisDataType.LARGEINT;
                    }
                } else if (scale > 0) {
                    if (precision > scale) {
                        dorisDataType = DorisDataType.DECIMAL;
                        dorisPrecision = precision;
                        dorisScale = scale;
                    } else {
                        dorisDataType = DorisDataType.DECIMAL;
                        dorisPrecision = scale;
                        dorisScale = scale;
                    }
                } else if (scale < 0) {
                    // s<0 的情况下，Doris 会将 p 设置为 p+|s|，并进行和 number(p) / number(p,0) 一样的映射
                    precision = precision - scale;
                    if (precision == 1) {
                        // 特殊处理number(1)为字符类型，Seatunnel会识别为布尔值，但scale的影响未知
                        dorisDataType = DorisDataType.TINYINT;
                        dorisDataTypeToChar = true;
                    } else if (precision < 3) {
                        dorisDataType = DorisDataType.TINYINT;
                    } else if (precision < 5) {
                        dorisDataType = DorisDataType.SMALLINT;
                    } else if (precision < 10) {
                        dorisDataType = DorisDataType.INT;
                    } else if (precision < 19) {
                        dorisDataType = DorisDataType.BIGINT;
                    } else {
                        dorisDataType = DorisDataType.LARGEINT;
                    }
                } else {
                    error = "不支持的的number(p,s)类型: number(" + precision + "," + scale + ")";
                }
            }
            case "uniqueidentifier" -> {
                dorisDataType = DorisDataType.VARCHAR;
                dorisLength = length * 4;
            }
            case "sysname" -> {
                dorisDataType = DorisDataType.VARCHAR;
                dorisLength = length * 4;
            }
            
            default -> {
                error = "表:%s -> 列:%s -> 不支持的数据类型 => dataType=%s, length=%s, precision=%s, scale=%s"
                        .formatted(column.getId().getTableName(), column.getId().getColumnName(), dataType, length, precision, scale);
            }
        }
        
        column.setTransDataType(dorisDataType);
        column.setTransLength(dorisLength);
        column.setTransPrecision(dorisPrecision);
        column.setTransScale(dorisScale);
        column.setTransSort(column.getSort());
    }
    
    @Override
    public String identifierDelimiter() {
        return "\"";
    }
    
    @Override
    public String timeTypeFormat(SourceTimeType incrementType) {
        if (SourceTimeType.YYYYMMDD.equals(incrementType)) {
            return "YYYYMMDD";
        } else if (SourceTimeType.YYYY_MM_DD.equals(incrementType)) {
            return "YYYY-MM-DD";
        } else if (SourceTimeType.YYYYMMDDHHMMSS.equals(incrementType)) {
            return "YYYYMMDDHH24MISS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S1.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S2.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S3.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S4.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S5.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S6.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS";
        }
        throw new RuntimeException("未配置时间格式");
    }
    
    @Override
    public String incrementCondition(List<SourceColumn> columns, String offsetBeforeDay) {
        String delimiter = identifierDelimiter();
        String dataType;
        SourceTimeType timeType;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < columns.size(); i++) {
            if (i == 0) {
                sb.append(" ${collectAll} WHERE ");
            } else {
                sb.append(" OR ");
            }
            SourceColumn column = columns.get(i);
            dataType = column.getDataType();
            String newColumnName = delimiter + column.getId().getColumnName() + delimiter;
            if (Strings.CI.equalsAny(dataType, "DATE", "TIMESTAMP", "TIMESTAMP(0)", "TIMESTAMP(3)", "TIMESTAMP(6)")) {
                sb.append("%s >= DATEADD(DAY, -%s, CAST(GETDATE() AS DATE))".formatted(newColumnName, offsetBeforeDay));
            } else if (Strings.CI.equalsAny(dataType, "CHAR", "NCHAR", "VARCHAR2", "NVARCHAR2")) {
                timeType = column.getTimeType();
                if (SourceTimeType.YYYYMMDD.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(8), DATEADD(DAY, -%s, CAST(GETDATE() AS DATE)), 112)".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(10), DATEADD(DAY, -%s, CAST(GETDATE() AS DATE)), 23)".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYYMMDDHHMMSS.equals(timeType)) {
                    sb.append("%s >= REPLACE(REPLACE(REPLACE(CONVERT(VARCHAR(19), DATEADD(DAY, -%s, CAST(GETDATE() AS DATE)), '-', ''), ' ', ''), ':', '')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(19), DATEADD(DAY, -%s, CAST(GETDATE() AS DATE)), 120)".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S1.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(21), DATEADD(DAY, -%s, CAST(SYSDATETIME() AS DATE)), 121)".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S2.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(22), DATEADD(DAY, -%s, CAST(SYSDATETIME() AS DATE)), 121)".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S3.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(23), DATEADD(DAY, -%s, CAST(SYSDATETIME() AS DATE)), 121)".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S4.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(24), DATEADD(DAY, -%s, CAST(SYSDATETIME() AS DATE)), 121)".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S5.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(25), DATEADD(DAY, -%s, CAST(SYSDATETIME() AS DATE)), 121)".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S6.equals(timeType)) {
                    sb.append("%s >= CONVERT(VARCHAR(26), DATEADD(DAY, -%s, CAST(SYSDATETIME() AS DATE)), 121)".formatted(newColumnName, offsetBeforeDay));
                }
            } else {
                throw new RuntimeException("Oracle增量字段不支持数据类型:" + dataType);
            }
        }
        return sb.toString();
    }
}