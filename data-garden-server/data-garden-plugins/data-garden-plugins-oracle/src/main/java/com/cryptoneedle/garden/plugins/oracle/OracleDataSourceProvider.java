package com.cryptoneedle.garden.plugins.oracle;

import com.cryptoneedle.garden.common.constants.CommonConstant;
import com.cryptoneedle.garden.common.enums.SourceConnectType;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.spi.DataSourceProvider;
import com.cryptoneedle.garden.spi.SshUtil;
import org.apache.commons.lang3.StringUtils;

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
            AND owner NOT IN (
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
            AND owner NOT LIKE 'APEX%'
            AND owner NOT LIKE 'FLOWS_%'""";
    
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
                SELECT owner                                                                                  AS "databaseName"          -- 数据库
                     , SUM(CASE WHEN object_type IN ('TABLE', 'VIEW', 'MATERIALIZED VIEW') THEN 1 ELSE 0 END) AS "totalNum"            -- 总数(表数量+视图数量+物化视图数量)
                     , SUM(CASE WHEN object_type = 'TABLE' THEN 1 ELSE 0 END)                                 AS "tableNum"            -- 表数量
                     , SUM(CASE WHEN object_type = 'VIEW' THEN 1 ELSE 0 END)                                  AS "viewNum"             -- 视图数量
                     , SUM(CASE WHEN object_type = 'MATERIALIZED VIEW' THEN 1 ELSE 0 END)                     AS "materializedViewNum" -- 物化视图数量
                 FROM all_objects
                WHERE 1 = 1
                  %s
                  %s
                GROUP BY owner
                ORDER BY databaseName""".formatted(FILTER_SYSTEM_DATABASE_CONDITION, searchDatabaseCondition);
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
                 ORDER BY t.owner, t.table_name""".formatted(searchDatabaseCondition, searchTableCondition);
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
                 ORDER BY t.owner, t.view_name""".formatted(searchDatabaseCondition, searchViewCondition);
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
                ORDER BY t.owner, t.mview_name""".formatted(searchDatabaseCondition, searchViewCondition);
    }
    
    @Override
    public String columnSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND tc.owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND tc.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT tc.owner                       AS "databaseName"   -- 数据库
                     , tc.table_name                  AS "tableName"      -- 表
                     , tc.column_name                 AS "columnName"     -- 字段
                     , c.comments                     AS "comment"        -- 字段说明
                     , tc.column_id                   AS "sort"           -- 排序
                     , tc.data_type                   AS "dataType"       -- 数据类型
                     , tc.data_length                 AS "length"         -- 长度
                     , tc.data_precision              AS "precision"      -- 精度
                     , tc.data_scale                  AS "scale"          -- 标度
                     , DECODE(tc.nullable, 'Y', 1, 0) AS "notNull"        -- 不可空
                     , tc.sample_size                 AS "sampleNum"      -- 采样数据量
                     , tc.num_nulls                   AS "nullNum"        -- 采样空值数据量
                     , tc.num_distinct                AS "distinctNum"    -- 采样基数
                     , tc.density                     AS "density"        -- 采样数据密度
                     , tc.low_value                   AS "minValue"       -- 采样最小值
                     , tc.high_value                  AS "maxValue"       -- 采样最大值
                     , tc.avg_col_len                 AS "avgColumnBytes" -- 字段平均占用空间(单位：Byte)
                     , tc.last_analyzed               AS "statisticDt"    -- 统计时间
                  FROM all_tab_columns tc
                         LEFT JOIN all_col_comments c ON tc.owner = c.owner AND tc.table_name = c.table_name AND tc.column_name = c.column_name
                 WHERE 1 = 1
                   %s
                   %s
                 ORDER BY tc.owner, tc.table_name, tc.column_id, tc.column_name""".formatted(searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String primaryConstraintSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND c.owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND c.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT c.owner              AS "databaseName"  -- 数据库
                     , c.table_name         AS "tableName"     -- 表
                     , 'PRIMARY_CONSTRAINT' AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , c.constraint_name    AS "dimensionName" -- 维度
                     , cc.column_name       AS "columnName"    -- 字段
                     , cc.position          AS "sort"          -- 排序
                  FROM all_constraints c
                         INNER JOIN all_cons_columns cc ON c.owner = cc.owner AND c.constraint_name = cc.constraint_name
                 WHERE c.constraint_type = 'P'
                   AND c.status = 'ENABLED'
                   %s
                   %s
                ORDER BY c.owner, c.table_name, c.constraint_name, cc.position""".formatted(searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String uniqueConstraintSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND c.owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND c.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT c.owner              AS "databaseName"  -- 数据库
                     , c.table_name         AS "tableName"     -- 表
                     , 'UNIQUE_CONSTRAINT'  AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , c.constraint_name    AS "dimensionName" -- 维度
                     , cc.column_name       AS "columnName"    -- 字段
                     , cc.position          AS "sort"          -- 排序
                  FROM all_constraints c
                         INNER JOIN all_cons_columns cc ON c.owner = cc.owner AND c.constraint_name = cc.constraint_name
                 WHERE c.constraint_type = 'U'
                   AND c.status = 'ENABLED'
                   %s
                   %s
                ORDER BY c.owner, c.table_name, c.constraint_name, cc.position""".formatted(searchDatabaseCondition, searchTableCondition);
    }
    
    @Override
    public String uniqueIndexSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND i.table_owner = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND i.table_name = '%s'".formatted(tableName) : "";
        // todo 暂时只进行非DBA用户的查询
        return """
                SELECT i.table_owner     AS "databaseName"  -- 数据库
                     , i.table_name      AS "tableName"     -- 表
                     , 'UNIQUE_INDEX'    AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , i.index_name      AS "dimensionName" -- 维度
                     , c.column_name     AS "columnName"    -- 字段
                     , c.column_position AS "sort"          -- 排序
                  FROM all_indexes i
                         INNER JOIN all_ind_columns c ON i.index_name = c.index_name AND i.table_owner = c.index_owner AND i.table_owner = c.table_owner
                 WHERE 1 = 1
                   %s
                   %s
                ORDER BY i.table_owner, i.table_name, i.index_name, c.column_position""".formatted(searchDatabaseCondition, searchTableCondition);
    }
}