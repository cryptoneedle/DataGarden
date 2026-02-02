package com.cryptoneedle.garden.spi;

import cn.hutool.v7.db.handler.ResultSetUtil;
import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import com.cryptoneedle.garden.common.enums.SourceTableType;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.common.key.source.SourceDimensionColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>description: 数据源-执行器 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-02
 */
@Slf4j
public class DataSourceExecutor {
    
    public static String version(SourceCatalog catalog) {
        DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
        
        // 使用Connection元数据方式获取
        try (Connection connection = DataSourceManager.getConnection(catalog)) {
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseMajorVersion() + "." + metaData.getDatabaseMinorVersion();
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库版本失败", e);
        }
        
        // todo 使用SQL查询方式
    }
    
    public static List<SourceDatabase> databases(SourceCatalog catalog, SourceDatabase database) {
        return DataSourceManager.getJdbcTemplate(catalog)
                                .query(DataSourceSpiLoader.getProvider(catalog.getDatabaseType())
                                                          .databaseSql(database != null ? database.getId().getDatabaseName() : null),
                                        (rs, rowNum) ->
                                                ResultSetUtil.toBean(rs.getMetaData(), rs, SourceDatabase.class)
                                                             .setId(ResultSetUtil.toBean(rs.getMetaData(), rs, SourceDatabaseKey.class)
                                                                                 .setCatalogName(catalog.getId()
                                                                                                        .getCatalogName())));
    }
    
    public static List<SourceTable> tables(SourceCatalog catalog, String databaseName, String tableName) {
        DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
        JdbcTemplate jdbcTemplate = DataSourceManager.getJdbcTemplate(catalog);
        
        List<SourceTable> tables = tables(jdbcTemplate, provider.tableSql(databaseName, tableName), catalog, SourceTableType.TABLE);
        List<SourceTable> views = tables(jdbcTemplate, provider.viewSql(databaseName, tableName), catalog, SourceTableType.VIEW);
        List<SourceTable> materializedView = tables(jdbcTemplate, provider.materializedViewSql(databaseName, tableName), catalog, SourceTableType.MATERIALIZED_VIEW);
        
        List<SourceTable> result = Lists.newArrayListWithCapacity(tables.size() + views.size() + materializedView.size());
        result.addAll(tables);
        result.addAll(views);
        result.addAll(materializedView);
        return result;
    }
    
    private static List<SourceTable> tables(JdbcTemplate jdbcTemplate, String sql, SourceCatalog catalog, SourceTableType sourceTableType) {
        return jdbcTemplate
                .query(sql, (rs, rowNum) ->
                        ResultSetUtil.toBean(rs.getMetaData(), rs, SourceTable.class)
                                     .setId(ResultSetUtil.toBean(rs.getMetaData(), rs, SourceTableKey.class)
                                                         .setCatalogName(catalog.getId().getCatalogName()))
                                     .setTableType(sourceTableType));
    }
    
    public static List<SourceColumn> columns(SourceCatalog catalog, String databaseName, String tableName) {
        return DataSourceManager.getJdbcTemplate(catalog)
                                .query(DataSourceSpiLoader.getProvider(catalog.getDatabaseType())
                                                          .columnSql(databaseName, tableName),
                                        (rs, rowNum) ->
                                                ResultSetUtil.toBean(rs.getMetaData(), rs, SourceColumn.class)
                                                             .setId(ResultSetUtil.toBean(rs.getMetaData(), rs, SourceColumnKey.class)
                                                                                 .setCatalogName(catalog.getId()
                                                                                                        .getCatalogName())));
    }
    
    public static List<SourceDimension> dimensions(SourceCatalog catalog, String databaseName, String tableName) {
        DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
        JdbcTemplate jdbcTemplate = DataSourceManager.getJdbcTemplate(catalog);
        
        List<SourceDimension> primaryConstraints = dimensions(jdbcTemplate, provider.primaryConstraintSql(databaseName, tableName), catalog, SourceDimensionType.PRIMARY_CONSTRAINT);
        List<SourceDimension> uniqueConstraints = dimensions(jdbcTemplate, provider.uniqueConstraintSql(databaseName, tableName), catalog, SourceDimensionType.UNIQUE_CONSTRAINT);
        List<SourceDimension> uniqueIndexs = dimensions(jdbcTemplate, provider.uniqueIndexSql(databaseName, tableName), catalog, SourceDimensionType.UNIQUE_INDEX);
        
        List<String> primaryDimensionNames = primaryConstraints.stream().map(dimension -> dimension.getId().commonDimensionNameTable()).distinct().toList();
        
        List<SourceDimension> result = Lists.newArrayListWithCapacity(primaryConstraints.size() + uniqueConstraints.size() + uniqueIndexs.size());
        result.addAll(primaryConstraints);
        result.addAll(uniqueConstraints);
        // 发现在Oracle中存在 PRIMARY_CONSTRAINT 与 UNIQUE_INDEX 的内容可能存在重复
        result.addAll(uniqueIndexs.stream().filter(idx -> !primaryDimensionNames.contains(idx.getId().commonDimensionNameTable())).toList());
        return result;
    }
    
    private static List<SourceDimension> dimensions(JdbcTemplate jdbcTemplate, String sql, SourceCatalog catalog, SourceDimensionType sourceDimensionType) {
        return jdbcTemplate
                .query(sql, (rs, rowNum) ->
                        ResultSetUtil.toBean(rs.getMetaData(), rs, SourceDimension.class)
                                     .setId(ResultSetUtil.toBean(rs.getMetaData(), rs, SourceDimensionColumnKey.class)
                                                         .setCatalogName(catalog.getId().getCatalogName())
                                                         .setDimensionType(sourceDimensionType)));
    }
    
    public static Long selectRowNum(SourceCatalog catalog, SourceTable table) {
        try {
            log.info("获取数据量:" + table.getId().getTableName());
            String sql = "SELECT COUNT(*) FROM %s.%s".formatted(table.getId().getDatabaseName(), table.getId().getTableName());
            String result = DataSourceManager.getJdbcTemplate(catalog).queryForObject(sql, String.class);
            return Long.valueOf(result);
        } catch (Exception e) {
            log.info("获取数据量失败", e);
        }
        return null;
    }
}