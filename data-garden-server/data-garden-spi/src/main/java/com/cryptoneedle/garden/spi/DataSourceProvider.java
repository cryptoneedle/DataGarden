package com.cryptoneedle.garden.spi;

import com.cryptoneedle.garden.common.enums.SourceTimeType;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceColumn;

import java.util.List;

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
    String databaseType();
    
    /**
     * 构建JDBC连接URL
     *
     * @param catalog 数据源目录
     * @return JDBC URL
     */
    String buildJdbcUrl(SourceCatalog catalog);
    
    /**
     * 获取数据库信息查询SQL
     * - databaseName: 数据库
     * - totalNum: 总数(表数量+视图数量+物化视图数量)
     * - tableNum: 表数量
     * - viewNum: 视图数量
     * - materializedViewNum: 物化视图数量
     *
     * @param databaseName 数据库(可以为空)
     * @return 数据库信息查询SQL
     */
    String databaseSql(String databaseName);
    
    /**
     * 获取表信息查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - comment: 表说明
     * - tableType: 表类型 [TABLE("表"),VIEW("视图")]
     * - rowNum: 数据量
     * - avgRowBytes: 行平均占用空间(单位：Byte)
     * - statisticDt: 统计时间
     *
     * @param databaseName 数据库(可以为空)
     * @param tableName    表(可以为空)
     * @return 表信息查询SQL
     */
    String tableSql(String databaseName, String tableName);
    
    /**
     * 获取视图信息查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - comment: 表说明
     * - tableType: 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
     * - rowNum: 数据量
     * - avgRowBytes: 行平均占用空间(单位：Byte)
     * - statisticDt: 统计时间
     *
     * @param databaseName 数据库(可以为空)
     * @param viewName     视图(可以为空)
     * @return 视图信息查询SQL
     */
    String viewSql(String databaseName, String viewName);
    
    /**
     * 获取物化视图信息查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - comment: 表说明
     * - tableType: 表类型 [TABLE("表"),VIEW("视图"),MATERIALIZED_VIEW("物化视图")]
     * - rowNum: 数据量
     * - avgRowBytes: 行平均占用空间(单位：Byte)
     * - statisticDt: 统计时间
     *
     * @param databaseName 数据库(可以为空)
     * @param viewName     物化视图(可以为空)
     * @return 物化视图信息查询SQL
     */
    String materializedViewSql(String databaseName, String viewName);
    
    /**
     * 获取字段信息查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - columnName: 字段
     * - comment: 字段说明
     * - sort: 排序
     * - dataType: 数据类型
     * - length: 长度
     * - precision: 精度
     * - scale: 标度
     * - notNull: 不可空
     * - sampleNum: 采样数据量
     * - nullNum: 采样空值数据量
     * - distinctNum: 采样基数
     * - density: 采样数据密度
     * - minValue: 采样最小值
     * - maxValue: 采样最大值
     * - avgColumnBytes: 字段平均占用空间(单位：Byte)
     * - statisticDt: 统计时间
     *
     * @param databaseName 数据库(可以为空)
     * @param tableName    表(可以为空)
     * @return 字段信息查询SQL
     */
    String columnSql(String databaseName, String tableName);
    
    /**
     * 获取主键查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - dimensionType: 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
     * - dimensionName: 维度
     * - columnName: 字段
     * - sort: 排序
     *
     * @param databaseName 数据库(可以为空)
     * @param tableName    表(可以为空)
     * @return 主键查询SQL
     */
    String primaryConstraintSql(String databaseName, String tableName);
    
    /**
     * 获取唯一键查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - dimensionType: 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
     * - dimensionName: 维度
     * - columnName: 字段
     * - sort: 排序
     *
     * @param databaseName 数据库(可以为空)
     * @param tableName    表(可以为空)
     * @return 唯一键查询SQL
     */
    String uniqueConstraintSql(String databaseName, String tableName);
    
    /**
     * 获取唯一索引查询SQL
     * - databaseName: 数据库
     * - tableName: 表
     * - dimensionType: 维度类型 [PRIMARY_CONSTRAINT("主键约束", 2),UNIQUE_CONSTRAINT("唯一键约束", 3),UNIQUE_INDEX("唯一索引", 4)]
     * - dimensionName: 维度
     * - columnName: 字段
     * - sort: 排序
     *
     * @param databaseName 数据库(可以为空)
     * @param tableName    表(可以为空)
     * @return 唯一索引查询SQL
     */
    String uniqueIndexSql(String databaseName, String tableName);
    
    /**
     * 转换列信息
     *
     * @param column 列信息
     */
    void transform(SourceColumn column);
    
    /**
     * 关键词字段包裹标记
     *
     * @return 关键词字段包裹标记
     */
    String identifierDelimiter();
    
    String timeTypeFormat(SourceTimeType incrementType);
    
    String incrementCondition(List<SourceColumn> columns, String offsetBeforeDay);
}