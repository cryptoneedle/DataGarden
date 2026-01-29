package com.cryptoneedle.garden.plugins.oracle;

import com.cryptoneedle.garden.common.constants.CommonConstant;
import com.cryptoneedle.garden.common.enums.DorisDataType;
import com.cryptoneedle.garden.common.enums.SourceConnectType;
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
public class OracleDataSourceProvider implements DataSourceProvider {
    
    private final String JDBC_URL_SERVICE_NAME_TEMPLATE = "jdbc:oracle:thin:@//%s:%s/%s";
    private final String JDBC_URL_SID_TEMPLATE = "jdbc:oracle:thin:@%s:%s:%s";
    
    private final String FILTER_SYSTEM_DATABASE_CONDITION = """
            AND t.owner NOT IN (
              -- 系统核心
              'SYS', 'SYSTEM', 'OUTLN', 'DBSNMP', 'ANONYMOUS', 'XS$NULL', 'DIP', 'ORACLE_OCM',
              -- 备份恢复
              'SYSBACKUP', 'SYSDG', 'SYSKM', 'SYSRAC',
              -- 功能组件
              'CTXSYS', 'MDSYS', 'WMSYS', 'XDB', 'ORDSYS', 'ORDDATA', 'ORDPLUGINS', 'SI_INFORMTN_SCHEMA',
              -- APEX相关
              'APEX_030200', 'APEX_PUBLIC_USER', 'FLOWS_FILES',
              -- 数据仓库
              'OLAPSYS', 'DMSYS', 'ODM', 'ODM_MTR',
              -- OWB (Oracle Warehouse Builder)
              'OWBSYS', 'OWBSYS_AUDIT',
              -- 其他组件
              'EXFSYS', 'LBACSYS', 'SYSMAN', 'MGMT_VIEW', 'APPQOSSYS', 'OJVMSYS', 'DBSFWUSER', 'TSMSYS', 'AUDSYS',
              -- 空间数据相关
              'SPATIAL_CSW_ADMIN_USR', 'SPATIAL_WFS_ADMIN_USR', 'MDDATA',
              -- 示例库
              'SCOTT', 'HR', 'OE', 'PM', 'SH', 'IX', 'BI',
              -- 其他
              'PERFSTAT', 'SQLTXPLAIN', 'TRACESVR', 'DVSYS', 'DVF', 'GSMADMIN_INTERNAL', 'GGSYS', 'REMOTE_SCHEDULER_AGENT'
            )
            AND t.owner NOT LIKE 'APEX%'
            AND t.owner NOT LIKE 'FLOWS_%'""";
    
    @Override
    public String databaseType() {
        return "ORACLE";
    }
    
    @Override
    public String buildJdbcUrl(SourceCatalog catalog) {
        String connectType = catalog.getConnectType();
        if (catalog.getConfigSsh() != null) {
            Integer forwardPort = SshUtil.getForwardPort(catalog);
            if (SourceConnectType.SERVICE_NAME.equals(SourceConnectType.valueOf(connectType))) {
                return JDBC_URL_SERVICE_NAME_TEMPLATE.formatted(CommonConstant.LOCAL_HOST, forwardPort, catalog.getRoute());
            } else if (SourceConnectType.SID.equals(SourceConnectType.valueOf(connectType))) {
                return JDBC_URL_SID_TEMPLATE.formatted(CommonConstant.LOCAL_HOST, forwardPort, catalog.getRoute());
            } else {
                throw new RuntimeException("[ORACLE] 不支持的链接类型" + connectType);
            }
        } else {
            if (SourceConnectType.SERVICE_NAME.equals(SourceConnectType.valueOf(connectType))) {
                return JDBC_URL_SERVICE_NAME_TEMPLATE.formatted(catalog.getHost(), catalog.getPort(), catalog.getRoute());
            } else if (SourceConnectType.SID.equals(SourceConnectType.valueOf(connectType))) {
                return JDBC_URL_SID_TEMPLATE.formatted(catalog.getHost(), catalog.getPort(), catalog.getRoute());
            } else {
                throw new RuntimeException("[ORACLE] 不支持的链接类型" + connectType);
            }
        }
    }
    
    @Override
    public String databaseSql(String databaseName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND owner = '%s'".formatted(databaseName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT owner                                                                                  AS "databaseName"        -- 数据库
                     , SUM(CASE WHEN object_type IN ('TABLE', 'VIEW', 'MATERIALIZED VIEW') THEN 1 ELSE 0 END) AS "totalNum"            -- 总数(表数量+视图数量+物化视图数量)
                     , SUM(CASE WHEN object_type = 'TABLE' THEN 1 ELSE 0 END)                                 AS "tableNum"            -- 表数量
                     , SUM(CASE WHEN object_type = 'VIEW' THEN 1 ELSE 0 END)                                  AS "viewNum"             -- 视图数量
                     , SUM(CASE WHEN object_type = 'MATERIALIZED VIEW' THEN 1 ELSE 0 END)                     AS "materializedViewNum" -- 物化视图数量
                 FROM all_objects t
                WHERE 1 = 1
                  %s
                  %s
                GROUP BY owner
                ORDER BY owner""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition);
    }
    
    @Override
    public String tableSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND t.owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT t.owner         AS "databaseName" -- 数据库
                     , t.table_name    AS "tableName"    -- 表
                     , c.comments      AS "comment"      -- 表说明
                     , 'TABLE'         AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , t.num_rows      AS "rowNum"       -- 数据量
                     , t.avg_row_len   AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , t.last_analyzed AS "statisticDt"  -- 统计时间
                  FROM all_tables t
                         LEFT JOIN all_tab_comments c ON t.owner = c.owner AND t.table_name = c.table_name AND c.table_type = 'TABLE'
                 WHERE 1 = 1
                   %s
                   %s
                   %s
                 ORDER BY t.owner, t.table_name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String viewSql(String databaseName, String viewName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND t.owner = '%s'".formatted(databaseName) : "";
        String searchViewCondition = StringUtils.isNotBlank(viewName) ? "AND t.view_name = '%s'".formatted(viewName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT t.owner      AS "databaseName" -- 数据库
                     , t.view_name  AS "tableName"    -- 表
                     , c.comments   AS "comment"      -- 表说明
                     , 'VIEW'       AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , NULL         AS "rowNum"       -- 数据量
                     , NULL         AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , NULL         AS "statisticDt"  -- 统计时间
                  FROM all_views t
                         LEFT JOIN all_tab_comments c ON t.owner = c.owner AND t.view_name = c.table_name AND c.table_type = 'VIEW'
                 WHERE 1 = 1
                   %s
                   %s
                   %s
                 ORDER BY t.owner, t.view_name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchViewCondition);
    }
    
    @Override
    public String materializedViewSql(String databaseName, String viewName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND t.owner = '%s'".formatted(databaseName) : "";
        String searchViewCondition = StringUtils.isNotBlank(viewName) ? "AND t.mview_name = '%s'".formatted(viewName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT t.owner             AS "databaseName" -- 数据库
                     , t.mview_name        AS "tableName"    -- 表
                     , c.comments          AS "comment"      -- 表说明
                     , 'MATERIALIZED_VIEW' AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , NULL                AS "rowNum"       -- 数据量
                     , NULL                AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , NULL                AS "statisticDt"  -- 统计时间
                FROM all_mviews t
                       LEFT JOIN all_mview_comments c ON t.owner = c.owner AND t.mview_name = c.mview_name
                WHERE 1 = 1
                  %s
                  %s
                  %s
                ORDER BY t.owner, t.mview_name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchViewCondition);
    }
    
    @Override
    public String columnSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND t.owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT t.owner                       AS "databaseName"   -- 数据库
                     , t.table_name                  AS "tableName"      -- 表
                     , t.column_name                 AS "columnName"     -- 字段
                     , c.comments                    AS "comment"        -- 字段说明
                     , t.column_id                   AS "sort"           -- 排序
                     , t.data_type                   AS "dataType"       -- 数据类型
                     , t.data_length                 AS "length"         -- 长度
                     , t.data_precision              AS "precision"      -- 精度
                     , t.data_scale                  AS "scale"          -- 标度
                     , DECODE(t.nullable, 'Y', 1, 0) AS "notNull"        -- 不可空
                     , t.sample_size                 AS "sampleNum"      -- 采样数据量
                     , t.num_nulls                   AS "nullNum"        -- 采样空值数据量
                     , t.num_distinct                AS "distinctNum"    -- 采样基数
                     , t.density                     AS "density"        -- 采样数据密度
                     , t.low_value                   AS "minValue"       -- 采样最小值
                     , t.high_value                  AS "maxValue"       -- 采样最大值
                     , t.avg_col_len                 AS "avgColumnBytes" -- 字段平均占用空间(单位：Byte)
                     , t.last_analyzed               AS "statisticDt"    -- 统计时间
                  FROM all_tab_columns t
                         LEFT JOIN all_col_comments c ON t.owner = c.owner AND t.table_name = c.table_name AND t.column_name = c.column_name
                 WHERE 1 = 1
                   %s
                   %s
                   %s
                 ORDER BY t.owner, t.table_name, t.column_id, t.column_name""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String primaryConstraintSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND t.owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT t.owner              AS "databaseName"  -- 数据库
                     , t.table_name         AS "tableName"     -- 表
                     , 'PRIMARY_CONSTRAINT' AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , t.constraint_name    AS "dimensionName" -- 维度
                     , cc.column_name       AS "columnName"    -- 字段
                     , cc.position          AS "sort"          -- 排序
                  FROM all_constraints t
                         INNER JOIN all_cons_columns cc ON t.owner = cc.owner AND t.constraint_name = cc.constraint_name
                 WHERE t.constraint_type = 'P'
                   AND t.status = 'ENABLED'
                   %s
                   %s
                   %s
                ORDER BY t.owner, t.table_name, t.constraint_name, cc.position""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String uniqueConstraintSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND t.owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT t.owner              AS "databaseName"  -- 数据库
                     , t.table_name         AS "tableName"     -- 表
                     , 'UNIQUE_CONSTRAINT'  AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , t.constraint_name    AS "dimensionName" -- 维度
                     , cc.column_name       AS "columnName"    -- 字段
                     , cc.position          AS "sort"          -- 排序
                  FROM all_constraints t
                         INNER JOIN all_cons_columns cc ON t.owner = cc.owner AND t.constraint_name = cc.constraint_name
                 WHERE t.constraint_type = 'U'
                   AND t.status = 'ENABLED'
                   %s
                   %s
                   %s
                ORDER BY t.owner, t.table_name, t.constraint_name, cc.position""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String uniqueIndexSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND t.table_owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND t.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT t.table_owner     AS "databaseName"  -- 数据库
                     , t.table_name      AS "tableName"     -- 表
                     , 'UNIQUE_INDEX'    AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , t.index_name      AS "dimensionName" -- 维度
                     , c.column_name     AS "columnName"    -- 字段
                     , c.column_position AS "sort"          -- 排序
                  FROM all_indexes t
                         INNER JOIN all_ind_columns c ON t.index_name = c.index_name AND t.table_owner = c.index_owner AND t.table_owner = c.table_owner AND t.uniqueness = 'UNIQUE'
                 WHERE 1 = 1
                   %s
                   %s
                   %s
                ORDER BY t.table_owner, t.table_name, t.index_name, c.column_position""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition, searchTableCondition);
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
            case "NUMBER" -> {
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
            case "DECIMAL" -> {
                dorisDataType = DorisDataType.DECIMAL;
                dorisPrecision = precision;
                dorisScale = scale;
            }
            case "FLOAT" -> {
                dorisDataType = DorisDataType.DOUBLE;
                dorisPrecision = precision;
                dorisScale = scale;
            }
            case "REAL" -> {
                dorisDataType = DorisDataType.DOUBLE;
                dorisPrecision = precision;
                dorisScale = scale;
            }
            case "DATE" -> {
                // 不使用官方建议，尝试转换为 DorisDataType.DATE
                //dorisDataType = DorisDataType.DATETIME;
                dorisDataType = DorisDataType.DATE;
                //warn = "尝试性实验：Oracle DATE -> Doris DATE (官方建议为 DATETIME)";
            }
            case "TIMESTAMP" -> {
                dorisDataType = DorisDataType.DATETIME;
                dorisScale = scale;
                warn = "未指定精度的TIMESTAMP，scale=" + dorisScale;
            }
            case "CHAR" -> {
                dorisDataType = DorisDataType.CHAR;
                dorisLength = length;
            }
            case "NCHAR" -> {
                dorisDataType = DorisDataType.CHAR;
                dorisLength = length * 4;
            }
            case "VARCHAR2" -> {
                dorisDataType = DorisDataType.VARCHAR;
                dorisLength = length;
            }
            case "NVARCHAR2" -> {
                dorisDataType = DorisDataType.VARCHAR;
                dorisLength = length * 4;
            }
            case "LONG" -> {
                dorisDataType = DorisDataType.STRING;
            }
            case "RAW" -> {
                dorisDataType = DorisDataType.STRING;
            }
            case "LONG RAW" -> {
                dorisDataType = DorisDataType.STRING;
            }
            case "INTERVAL" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // 除官网外增加的额外支持项 todo 待观察采集效果
            case "CLOB" -> {
                dorisDataType = DorisDataType.STRING;
                warn = "CLOB";
            }
            case "BLOB" -> {
                dorisDataType = DorisDataType.STRING;
                warn = "BLOB";
            }
            // 精度到 秒
            case "TIMESTAMP(0)" -> {
                dorisDataType = DorisDataType.DATETIME;
                dorisScale = scale;
            }
            // 精度到 毫秒
            case "TIMESTAMP(3)" -> {
                dorisDataType = DorisDataType.DATETIME;
                dorisScale = scale;
            }
            // 精度到 微秒
            case "TIMESTAMP(6)" -> {
                dorisDataType = DorisDataType.DATETIME;
                dorisScale = scale;
            }
            // 精度到 纳秒，todo 使用DATETIME只支持到微妙，不支持纳秒，会有精度损失，后续可能需要转换为字符串处理
            //case "TIMESTAMP(9)" -> {
            //    dorisDataType = DorisDataType.DATETIME;
            //}
            // 暂不支持项(源于DataGrip中的默认类型)
            // BFILE
            // BINARY_DOUBLE
            // BINARY_FLOAT
            // CHARACTER
            // DEC
            // INTEGER
            // NATIONAL
            // NCLOB
            // NUMERIC
            // RAW
            // ROWID
            // UROWID
            // VARCHAR
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
        //column.setTransToChar(dorisDataTypeToChar);
        
        //        if (warn != null) {
        //            column.setTransWarn(warn);
        //            log.warn(warn);
        //        }
        //        if (error != null) {
        //            column.setTransError(error);
        //            log.error(error);
        //        }
    }
    
    @Override
    public String identifierDelimiter() {
        return "\"";
    }
    
    @Override
    public String validStrToDate(SourceColumn column) {
        String databaseName = column.getId().getDatabaseName();
        String tableName = column.getId().getTableName();
        String columnName = identifierDelimiter() + column.getId().getColumnName() + identifierDelimiter();
        return """
                SELECT CASE
                         WHEN EXISTS(
                           SELECT 1 FROM %s.%s
                            WHERE NOT (
                              LENGTH(%s) = 8 AND REGEXP_LIKE(%s, '^[0-9]{4}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$')
                              OR LENGTH(%s) = 10 AND REGEXP_LIKE(%s, '^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$')
                              OR LENGTH(%s) = 19 AND REGEXP_LIKE(%s, '^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) ([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$')
                            )
                         ) THEN 'false' ELSE 'true'
                       END AS is_date
                FROM DUAL""".formatted(databaseName, tableName, columnName, columnName, columnName, columnName, columnName, columnName);
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
        }
        throw new RuntimeException("未配置时间格式");
    }
    
    @Override
    public String incrementCondition(List<SourceColumn> columns, String offsetBeforeDay) {
        String delimiter = identifierDelimiter();
        String dataType;
        String timeType;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < columns.size(); i++) {
            if (i == 0) {
                sb.append(" ${collectAll} WHERE ");
            } else {
                sb.append(" OR ");
            }
            SourceColumn column = columns.get(i);
            dataType = column.getDataType();
            if (Strings.CI.equalsAny(dataType, "DATE", "TIMESTAMP", "TIMESTAMP(0)", "TIMESTAMP(3)", "TIMESTAMP(6)")) {
                sb.append("%s >= TRUNC(SYSDATE) - %s".formatted(delimiter + column.getId().getColumnName() + delimiter, offsetBeforeDay));
            } else if (Strings.CI.equalsAny(dataType, "CHAR", "NCHAR", "VARCHAR2", "NVARCHAR2")) {
                timeType = timeTypeFormat(column.getTimeType());
                sb.append("%s >= TO_CHAR((TRUNC(SYSDATE) - %s), '%s')".formatted(column.getId().getColumnName(), offsetBeforeDay, timeType));
            } else {
                throw new RuntimeException("Oracle增量字段不支持数据类型:" + dataType);
            }
        }
        return sb.toString();
    }
}