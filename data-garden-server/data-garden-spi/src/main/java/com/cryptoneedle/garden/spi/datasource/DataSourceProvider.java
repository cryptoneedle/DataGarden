package com.cryptoneedle.garden.spi.datasource;

import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;

/**
 * <p>description: 数据源-插件规范 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-29
 */
public interface DataSourceProvider {

    /**
     * 获取数据库类型
     *
     * @return 数据库类型
     */
    String getDatabaseType();

    /**
     * 获取JDBC驱动类名
     *
     * @return JDBC驱动类名
     */
    String getDriver();

    /**
     * 构建JDBC连接URL
     *
     * @param catalog 数据源目录
     * @return JDBC URL
     */
    String buildJdbcUrl(SourceCatalog catalog);

    /**
     * 获取数据库版本查询SQL
     * 如果不重写会使用 Connection 的方式获取
     *
     * @return 版本查询SQL
     */
    default String getVersionSql() {
        return null;
    };

    /**
     * 获取数据库信息查询SQL
     * - databaseName: 数据库
     *
     * @return 数据库信息查询SQL
     */
    String getDatabasesSql();

    /**
     * 获取表信息查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - comment: 表说明
     * - tableType: 表类型 [TABLE("表"),VIEW("视图")]
     *
     * @return 表信息查询SQL
     */
    String getTablesSql(String databaseName, String tableName);

    /**
     * 获取表统计信息查询SQL
     * 查询结果列要求：
     * - rowNum: 数据量
     * - columnNum: 字段数量
     * - avgRowBytes: 行平均占用空间(单位：Byte)
     * - statisticDt: 统计时间
     *
     * @return 表统计信息查询SQL
     */
    String getTableStatisticsSql();

    /**
     * 获取字段信息查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - columnName: 字段
     * - comment: 字段说明
     * - columnType: 字段类型 [UNIQUE("主键字段"), COMMON("普通字段")]
     * - dataTypeFormat: 数据类型格式化
     * - dataType: 数据类型
     * - length: 长度
     * - precision: 精度
     * - scale: 标度
     *
     * @return 字段信息查询SQL
     */
    String getColumnsSql(String databaseName, String tableName);

    /**
     * 获取字段统计信息查询SQL
     * - sampleNum: 采样数据量
     * - nullNum: 采样空值数据量
     * - distinctNum: 采样基数
     * - density: 采样数据密度
     * - minValue: 采样最小值
     * - maxValue: 采样最大值
     * - avgColumnBytes: 字段平均占用空间(单位：Byte)
     * 
     * @return 字段统计信息查询SQL
     */
    String getColumnStatisticsSql(String databaseName, String tableName);
}
