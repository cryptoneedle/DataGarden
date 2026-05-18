package com.cryptoneedle.garden.plugins.postgre;

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
 * <p>description: Postgre数据源元数据提供者实现 </p>
 * <p>
 * 提供 Postgre 数据库的元数据查询SQL定义，包括：
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
 * @date 2026-04-27
 */
public class PostgreDataSourceProvider implements DataSourceProvider {

    private final String JDBC_URL_TEMPLATE = "jdbc:postgresql://%s:%s/%s";

    private final String FILTER_SYSTEM_SCHEMA_CONDITION = """
            AND n.nspname NOT IN (
              'pg_catalog', 'information_schema'
            )
            AND n.nspname NOT LIKE 'pg_toast%'
            AND n.nspname NOT LIKE 'pg_temp%'""";

    @Override
    public String databaseType() {
        return "POSTGRESQL";
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
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND n.nspname = '%s'".formatted(databaseName) : "";
        return """
                SELECT n.nspname                                                                       AS "databaseName"        -- 数据库(Schema)
                     , COUNT(*)                                                                        AS "totalNum"            -- 总数(表数量+视图数量+物化视图数量)
                     , SUM(CASE WHEN c.relkind = 'r' THEN 1 ELSE 0 END)                                AS "tableNum"            -- 表数量
                     , SUM(CASE WHEN c.relkind = 'v' THEN 1 ELSE 0 END)           AS "viewNum"             -- 视图数量
                     , SUM(CASE WHEN c.relkind = 'm' THEN 1 ELSE 0 END)                                AS "materializedViewNum" -- 物化视图数量
                FROM pg_class c
                         INNER JOIN pg_namespace n ON c.relnamespace = n.oid
                WHERE c.relkind IN ('r', 'v', 'm')
                  %s
                  %s
                GROUP BY n.nspname
                ORDER BY n.nspname""".formatted(FILTER_SYSTEM_SCHEMA_CONDITION, searchDatabaseCondition);
    }

    @Override
    public String tableSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND n.nspname = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND c.relname = '%s'".formatted(tableName) : "";
        return """
                SELECT n.nspname                   AS "databaseName" -- 数据库(Schema)
                     , c.relname                   AS "tableName"    -- 表
                     , obj_description(c.oid)      AS "comment"      -- 表说明
                     , 'TABLE'                     AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , s.n_live_tup                AS "rowNum"       -- 数据量
                     , NULL                        AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , case
                           when s.last_autoanalyze > s.last_analyze then s.last_autoanalyze
                           else s.last_analyze end AS "statisticDt"  -- 统计时间
                FROM pg_class c
                         INNER JOIN pg_namespace n ON c.relnamespace = n.oid
                         LEFT JOIN pg_stat_user_tables s ON c.relname = s.relname AND n.nspname = s.schemaname
                WHERE c.relkind = 'r'
                  %s
                  %s
                  %s
                ORDER BY n.nspname, c.relname""".formatted(FILTER_SYSTEM_SCHEMA_CONDITION, searchDatabaseCondition, searchTableCondition);
    }

    @Override
    public String viewSql(String databaseName, String viewName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND n.nspname = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(viewName) ? "AND c.relname = '%s'".formatted(viewName) : "";
        return """
                SELECT n.nspname                               AS "databaseName" -- 数据库(Schema)
                     , c.relname                               AS "tableName"    -- 表
                     , obj_description(c.oid)                  AS "comment"      -- 表说明
                     , 'VIEW'                                  AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , NULL                                    AS "rowNum"       -- 数据量
                     , NULL                                    AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , NULL                                    AS "statisticDt"  -- 统计时间
                FROM pg_class c
                         INNER JOIN pg_namespace n ON c.relnamespace = n.oid
                WHERE c.relkind = 'v'
                  %s
                  %s
                  %s
                ORDER BY n.nspname, c.relname""".formatted(FILTER_SYSTEM_SCHEMA_CONDITION, searchDatabaseCondition, searchTableCondition);
    }

    @Override
    public String materializedViewSql(String databaseName, String viewName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND n.nspname = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(viewName) ? "AND c.relname = '%s'".formatted(viewName) : "";
        return """
                SELECT n.nspname                               AS "databaseName" -- 数据库(Schema)
                     , c.relname                               AS "tableName"    -- 表
                     , obj_description(c.oid)                  AS "comment"      -- 表说明
                     , 'MATERIALIZED_VIEW'                     AS "tableType"    -- 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
                     , s.n_live_tup                            AS "rowNum"       -- 数据量
                     , NULL                                    AS "avgRowBytes"  -- 行平均占用空间(单位：Byte)
                     , s.last_analyze                          AS "statisticDt"  -- 统计时间
                FROM pg_class c
                         INNER JOIN pg_namespace n ON c.relnamespace = n.oid
                         LEFT JOIN pg_stat_user_tables s ON c.relname = s.relname AND n.nspname = s.schemaname
                WHERE c.relkind = 'm'
                  %s
                  %s
                  %s
                ORDER BY n.nspname, c.relname""".formatted(FILTER_SYSTEM_SCHEMA_CONDITION, searchDatabaseCondition, searchTableCondition);
    }

    @Override
    public String columnSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND n.nspname = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND c.relname = '%s'".formatted(tableName) : "";
        return """
                SELECT n.nspname                                   AS "databaseName"   -- 数据库(Schema)
                     , c.relname                                   AS "tableName"      -- 表
                     , a.attname                                   AS "columnName"     -- 字段
                     , col_description(c.oid, a.attnum)            AS "comment"        -- 字段说明
                     , a.attnum                                    AS "sort"           -- 排序
                     , pg_catalog.format_type(a.atttypid, NULL)    AS "dataType"       -- 数据类型(不含参数)
                     , CASE
                           WHEN a.attlen = -1 AND a.atttypmod != -1
                               THEN a.atttypmod - 4
                           ELSE a.attlen END                       AS "length"         -- 长度
                     , CASE
                           WHEN t.typname IN ('numeric', 'decimal') AND a.atttypmod >= 0
                               THEN ((a.atttypmod - 4) >> 16) & 65535
                           ELSE NULL END                           AS "precision"      -- 精度
                     , CASE
                           WHEN t.typname IN ('numeric', 'decimal') AND a.atttypmod >= 0
                               THEN (a.atttypmod - 4) & 65535
                           ELSE NULL END                           AS "scale"          -- 标度
                     , CASE WHEN a.attnotnull THEN 1 ELSE 0 END    AS "notNull"        -- 不可空
                     , NULL                                        AS "sampleNum"      -- 采样数据量
                     , NULL                                        AS "nullNum"        -- 采样空值数据量
                     , NULL                                        AS "distinctNum"    -- 采样基数
                     , NULL                                        AS "density"        -- 采样数据密度
                     , NULL                                        AS "minValue"       -- 采样最小值
                     , NULL                                        AS "maxValue"       -- 采样最大值
                     , NULL                                        AS "avgColumnBytes" -- 字段平均占用空间(单位：Byte)
                     , NULL                                        AS "statisticDt"    -- 统计时间
                FROM pg_attribute a
                         INNER JOIN pg_class c ON a.attrelid = c.oid
                         INNER JOIN pg_namespace n ON c.relnamespace = n.oid
                         INNER JOIN pg_type t ON a.atttypid = t.oid
                WHERE a.attnum > 0
                  AND NOT a.attisdropped
                  %s
                  %s
                  %s
                ORDER BY n.nspname, c.relname, a.attnum, a.attname""".formatted(FILTER_SYSTEM_SCHEMA_CONDITION, searchDatabaseCondition, searchTableCondition);
    }

    @Override
    public String primaryConstraintSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND n.nspname = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND c.relname = '%s'".formatted(tableName) : "";
        return """
                SELECT n.nspname               AS "databaseName"  -- 数据库(Schema)
                     , c.relname               AS "tableName"     -- 表
                     , 'PRIMARY_CONSTRAINT'    AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , con.conname             AS "dimensionName" -- 维度
                     , a.attname               AS "columnName"    -- 字段
                     , array_position(con.conkey, a.attnum)       AS "sort"          -- 排序
                FROM pg_constraint con
                         INNER JOIN pg_class c ON con.conrelid = c.oid
                         INNER JOIN pg_namespace n ON c.relnamespace = n.oid
                         CROSS JOIN LATERAL unnest(con.conkey) AS col_attnum
                         INNER JOIN pg_attribute a ON a.attrelid = c.oid AND a.attnum = col_attnum
                WHERE con.contype = 'p'
                  %s
                  %s
                  %s
                ORDER BY n.nspname, c.relname, con.conname, array_position(con.conkey, a.attnum)""".formatted(FILTER_SYSTEM_SCHEMA_CONDITION, searchDatabaseCondition, searchTableCondition);
    }

    @Override
    public String uniqueConstraintSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND n.nspname = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND c.relname = '%s'".formatted(tableName) : "";
        return """
                SELECT n.nspname               AS "databaseName"  -- 数据库(Schema)
                     , c.relname               AS "tableName"     -- 表
                     , 'UNIQUE_CONSTRAINT'     AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , con.conname             AS "dimensionName" -- 维度
                     , a.attname               AS "columnName"    -- 字段
                     , array_position(con.conkey, a.attnum)       AS "sort"          -- 排序
                FROM pg_constraint con
                         INNER JOIN pg_class c ON con.conrelid = c.oid
                         INNER JOIN pg_namespace n ON c.relnamespace = n.oid
                         CROSS JOIN LATERAL unnest(con.conkey) AS col_attnum
                         INNER JOIN pg_attribute a ON a.attrelid = c.oid AND a.attnum = col_attnum
                WHERE con.contype = 'u'
                  %s
                  %s
                  %s
                ORDER BY n.nspname, c.relname, con.conname, array_position(con.conkey, a.attnum)""".formatted(FILTER_SYSTEM_SCHEMA_CONDITION, searchDatabaseCondition, searchTableCondition);
    }

    @Override
    public String uniqueIndexSql(String databaseName, String tableName) {
        String searchDatabaseCondition = StringUtils.isNotBlank(databaseName) ? "AND n.nspname = '%s'".formatted(databaseName) : "";
        String searchTableCondition = StringUtils.isNotBlank(tableName) ? "AND c.relname = '%s'".formatted(tableName) : "";
        return """
                SELECT n.nspname               AS "databaseName"  -- 数据库(Schema)
                     , c.relname               AS "tableName"     -- 表
                     , 'UNIQUE_INDEX'          AS "dimensionType" -- 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
                     , ic.relname              AS "dimensionName" -- 维度
                     , a.attname               AS "columnName"    -- 字段
                     , array_position(i.indkey, a.attnum)         AS "sort"          -- 排序
                FROM pg_index i
                         INNER JOIN pg_class c ON i.indrelid = c.oid
                         INNER JOIN pg_class ic ON i.indexrelid = ic.oid
                         INNER JOIN pg_namespace n ON c.relnamespace = n.oid
                         CROSS JOIN LATERAL unnest(i.indkey) AS col_attnum
                         INNER JOIN pg_attribute a ON a.attrelid = c.oid AND a.attnum = col_attnum
                WHERE i.indisunique = true
                  AND NOT EXISTS (
                      SELECT 1 FROM pg_constraint con
                      WHERE con.conrelid = c.oid
                        AND con.contype IN ('p', 'u')
                        AND con.conindid = i.indexrelid
                  )
                  %s
                  %s
                  %s
                ORDER BY n.nspname, c.relname, ic.relname, array_position(i.indkey, a.attnum)""".formatted(FILTER_SYSTEM_SCHEMA_CONDITION, searchDatabaseCondition, searchTableCondition);
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
            // ============ 布尔类型 ============
            case "boolean" -> {
                dorisDataType = DorisDataType.BOOLEAN;
            }
            // ============ 整数类型 ============
            case "smallint", "int2" -> {
                dorisDataType = DorisDataType.SMALLINT;
            }
            case "integer", "int", "int4" -> {
                dorisDataType = DorisDataType.INT;
            }
            case "bigint", "int8" -> {
                dorisDataType = DorisDataType.BIGINT;
            }
            // ============ 自增序列类型 ============
            case "smallserial" -> {
                dorisDataType = DorisDataType.SMALLINT;
            }
            case "serial" -> {
                dorisDataType = DorisDataType.INT;
            }
            case "bigserial" -> {
                dorisDataType = DorisDataType.BIGINT;
            }
            // ============ 浮点类型 ============
            case "real", "float4" -> {
                dorisDataType = DorisDataType.FLOAT;
                dorisPrecision = precision;
                dorisScale = scale;
            }
            case "double", "double precision", "float8" -> {
                dorisDataType = DorisDataType.DOUBLE;
                dorisPrecision = precision;
                dorisScale = scale;
            }
            // ============ 精确数值类型 ============
            case "numeric", "decimal" -> {
                if (precision == null && (scale == null || scale == -1)) {
                    // 无精度numeric映射为STRING（参考Doris官方文档）
                    dorisDataType = DorisDataType.VARCHAR;
                    dorisLength = 50L;
                    warn = "未指定precision和scale的numeric/decimal类型，映射为STRING";
                } else if (scale == null || scale == 0) {
                    // 整数位数的numeric
                    if (precision == 1) {
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
                    precision = precision - scale;
                    if (precision == 1) {
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
                    error = "不支持的numeric/decimal(p,s)类型: numeric(" + precision + "," + scale + ")";
                }
            }
            // ============ 字符类型 ============
            case "character", "char", "bpchar" -> {
                dorisDataType = DorisDataType.CHAR;
                if (length != null && length > 0) {
                    dorisLength = length * 4;
                }
            }
            case "character varying", "varchar" -> {
                dorisDataType = DorisDataType.VARCHAR;
                if (length != null && length > 0) {
                    dorisLength = length * 4;
                }
            }
            case "text" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ 日期时间类型 ============
            case "date" -> {
                dorisDataType = DorisDataType.DATE;
            }
            case "timestamp without time zone", "timestamp" -> {
                dorisDataType = DorisDataType.DATETIME;
                if (scale != null && scale > 0) {
                    dorisScale = scale;
                }
            }
            case "timestamp with time zone", "timestamptz" -> {
                dorisDataType = DorisDataType.DATETIME;
                if (scale != null && scale > 0) {
                    dorisScale = scale;
                }
                warn = "timestamp with time zone将转换为本地时区时间，请注意时区一致性";
            }
            case "time without time zone", "time" -> {
                // Doris不支持time类型，映射为STRING
                dorisDataType = DorisDataType.STRING;
            }
            case "time with time zone", "timetz" -> {
                dorisDataType = DorisDataType.STRING;
            }
            case "interval" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ JSON类型 ============
            case "json", "jsonb" -> {
                dorisDataType = DorisDataType.STRING;
                warn = "JSON/JSONB类型映射为STRING，以平衡读取和计算性能";
            }
            // ============ UUID类型 ============
            case "uuid" -> {
                dorisDataType = DorisDataType.VARCHAR;
                dorisLength = 36L;
            }
            // ============ 位串类型 ============
            case "bit" -> {
                if (length != null && length == 1) {
                    dorisDataType = DorisDataType.BOOLEAN;
                } else {
                    dorisDataType = DorisDataType.STRING;
                }
            }
            case "bit varying", "varbit" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ 二进制类型 ============
            case "bytea" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ 网络地址类型 ============
            case "cidr", "inet", "macaddr", "macaddr8" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ 几何类型 ============
            case "point", "line", "lseg", "box", "path", "polygon", "circle" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ 全文搜索类型 ============
            case "tsvector", "tsquery" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ 范围类型 ============
            case "int4range", "int8range", "numrange", "tsrange", "tstzrange", "daterange" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ 货币类型 ============
            case "money" -> {
                dorisDataType = DorisDataType.DECIMAL;
                dorisPrecision = 19L;
                dorisScale = 4L;
            }
            // ============ OID类型 ============
            case "oid", "regproc", "regprocedure", "regoper", "regoperator", "regclass", "regtype",
                 "regrole", "regnamespace", "regconfig", "regdictionary" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ XML类型 ============
            case "xml" -> {
                dorisDataType = DorisDataType.STRING;
            }
            // ============ 数组类型 ============
            // 注：format_type(a.atttypid, NULL) 对于数组类型返回如 "integer[]"
            // 但从pg_type获取时，数组类型的typname以"_"开头
            case "_bool", "_int2", "_int4", "_int8", "_float4", "_float8", "_numeric",
                 "_varchar", "_text", "_timestamp", "_timestamptz", "_date", "_json", "_jsonb",
                 "_uuid", "_bytea" -> {
                dorisDataType = DorisDataType.STRING;
                warn = "数组类型暂映射为STRING，需在数据写入后确定具体维度才能正确映射为ARRAY";
            }

            default -> {
                // 检查是否为数组类型（dataType以"_"开头或包含"[]"）
                if (dataType != null && (dataType.startsWith("_") || dataType.contains("[]"))) {
                    dorisDataType = DorisDataType.STRING;
                    warn = "数组类型暂映射为STRING，需在数据写入后确定具体维度才能正确映射为ARRAY";
                } else {
                    error = "表:%s -> 列:%s -> 不支持的数据类型 => dataType=%s, length=%s, precision=%s, scale=%s"
                            .formatted(column.getId().getTableName(), column.getId().getColumnName(), dataType, length, precision, scale);
                }
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
            return "YYYY-MM-DD HH24:MI:SS.US";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S2.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS.US";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S3.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS.MS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S4.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS.MS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S5.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS.MS";
        } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S6.equals(incrementType)) {
            return "YYYY-MM-DD HH24:MI:SS.US";
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
            if (Strings.CI.equalsAny(dataType, "date", "timestamp without time zone", "timestamp",
                    "timestamp with time zone", "timestamptz", "timestamp(0)", "timestamp(3)", "timestamp(6)")) {
                sb.append("%s >= CURRENT_DATE - INTERVAL '%s days'".formatted(newColumnName, offsetBeforeDay));
            } else if (Strings.CI.equalsAny(dataType, "character", "char", "character varying", "varchar", "text", "bpchar")) {
                timeType = column.getTimeType();
                if (SourceTimeType.YYYYMMDD.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYYMMDD')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYY-MM-DD')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYYMMDDHHMMSS.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYYMMDDHH24MISS')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYY-MM-DD HH24:MI:SS')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S1.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYY-MM-DD HH24:MI:SS.US')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S2.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYY-MM-DD HH24:MI:SS.US')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S3.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYY-MM-DD HH24:MI:SS.MS')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S4.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYY-MM-DD HH24:MI:SS.MS')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S5.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYY-MM-DD HH24:MI:SS.MS')".formatted(newColumnName, offsetBeforeDay));
                } else if (SourceTimeType.YYYY_MM_DD_HH_MM_SS_S6.equals(timeType)) {
                    sb.append("%s >= TO_CHAR(CURRENT_DATE - INTERVAL '%s days', 'YYYY-MM-DD HH24:MI:SS.US')".formatted(newColumnName, offsetBeforeDay));
                }
            } else {
                throw new RuntimeException("PostgreSQL增量字段不支持数据类型:" + dataType);
            }
        }
        return sb.toString();
    }
}
