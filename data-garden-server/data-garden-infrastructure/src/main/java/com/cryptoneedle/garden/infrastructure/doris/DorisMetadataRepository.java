package com.cryptoneedle.garden.infrastructure.doris;


import cn.hutool.v7.core.date.DateUtil;
import com.cryptoneedle.garden.common.constants.CommonConstant;
import com.cryptoneedle.garden.common.enums.DorisTableType;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.dto.*;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisShowCreateTable;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>description: Doris-元数据 </p>
 * <p>
 * 由于 Jpa 和 JdbcTemplate 限制，使用原生 Jdbc Api 调用 Doris 语句
 *
 * @author CryptoNeedle
 * @date 2025-09-25
 */
@Service
@Slf4j
public class DorisMetadataRepository {
    
    @Resource
    private JdbcTemplate dorisJdbcTemplate;
    
    public boolean execReturnBoolean(String sql) {
        boolean result = false;
        try {
            dorisJdbcTemplate.execute(sql);
            result  = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public void execDropCatalogIfExists(SourceCatalog catalog, boolean needTest) {
        String catalogPrefix = needTest ? "temp_test_" : "";
        String dorisCatalog = catalogPrefix + catalog.getDorisCatalog();
        
        String sql = String.format("DROP CATALOG IF EXISTS %s", dorisCatalog);
        dorisJdbcTemplate.execute(sql);
    }
    
    public void execDropCatalog(SourceCatalog catalog, boolean needTest) {
        String catalogPrefix = needTest ? "temp_test_" : "";
        String dorisCatalog = catalogPrefix + catalog.getDorisCatalog();
        
        String sql = String.format("DROP CATALOG %s", dorisCatalog);
        dorisJdbcTemplate.execute(sql);
    }
    
    public List<DorisExecShowCatalogs> showCatalogs() {
        List<DorisExecShowCatalogs> list = new ArrayList<>();
        
        String sql = "SHOW CATALOGS";
        try (Connection connection = dorisJdbcTemplate.getDataSource().getConnection()) {
            ResultSet rs = connection.prepareStatement(sql).executeQuery(sql);
            while (rs.next()) {
                DorisExecShowCatalogs domain = new DorisExecShowCatalogs();
                domain.setCatalogId(rs.getLong("CatalogId"));
                domain.setCatalogName(rs.getString("CatalogName"));
                domain.setType(rs.getString("Type"));
                domain.setIsCurrent(rs.getString("IsCurrent"));
                domain.setCreateTime(rs.getString("CreateTime"));
                domain.setLastUpdateTime(rs.getString("LastUpdateTime"));
                domain.setComment(rs.getString("Comment"));
                
                list.add(domain);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return list;
    }
    
    public List<DorisExecShowDatabases> showDatabases() {
        List<DorisExecShowDatabases> list = new ArrayList<>();
        
        String sql = "SHOW DATABASES";
        try (Connection connection = dorisJdbcTemplate.getDataSource().getConnection()) {
            ResultSet rs = connection.prepareStatement(sql).executeQuery(sql);
            while (rs.next()) {
                DorisExecShowDatabases domain = new DorisExecShowDatabases();
                domain.setCatalogName(rs.getString(CommonConstant.DORIS_CATALOG));
                domain.setDatabaseName(rs.getString("Database"));
                
                list.add(domain);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return list;
    }
    
    public List<DorisExecShowDatabases> showDatabasesFrom(String catalogName) {
        List<DorisExecShowDatabases> list = new ArrayList<>();
        
        String sql = "SHOW DATABASES FROM %s".formatted(catalogName);
        try (Connection connection = dorisJdbcTemplate.getDataSource().getConnection()) {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                DorisExecShowDatabases domain = new DorisExecShowDatabases();
                domain.setCatalogName(catalogName);
                domain.setDatabaseName(rs.getString("Database"));
                
                list.add(domain);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return list;
    }
    
    public List<DorisExecFullTables> execShowFullTables(String catalogName, String databaseName) {
        List<DorisExecFullTables> list = new ArrayList<>();
        
        String sql = "SHOW FULL TABLES FROM %s.%s".formatted(catalogName, databaseName);
        try (Connection connection = dorisJdbcTemplate.getDataSource().getConnection()) {
            connection.prepareStatement("use " + databaseName).execute();
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                DorisExecFullTables domain = new DorisExecFullTables();
                domain.setId(new DorisTableKey(databaseName, rs.getString("Tables_in_" + databaseName)));
                domain.setTableType(rs.getString("Table_type"));
                domain.setStorageFormat(rs.getString("Storage_format"));
                domain.setInvertedIndexStorageFormat(rs.getString("Inverted_index_storage_format"));
                
                list.add(domain);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return list;
    }
    
    public List<DorisExecShowTableStatus> execShowTableStatus(String catalogName, String databaseName) {
        List<DorisExecShowTableStatus> list = new ArrayList<>();
        
        String sql = "SHOW TABLE STATUS FROM %s.%s".formatted(catalogName, databaseName);
        try (Connection connection = dorisJdbcTemplate.getDataSource().getConnection()) {
            connection.prepareStatement("use " + databaseName).execute();
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                DorisExecShowTableStatus domain = new DorisExecShowTableStatus();
                domain.setId(new DorisTableKey(databaseName, rs.getString("Name")));
                domain.setEngine(rs.getString("Engine"));
                domain.setRows(rs.getLong("Rows"));
                domain.setAvgRowLength(rs.getLong("Avg_row_length"));
                domain.setDataLength(rs.getLong("Data_length"));
                domain.setCreateTime(rs.getString("Create_time"));
                domain.setUpdateTime(rs.getString("Update_time"));
                domain.setTableCollation(rs.getString("Collation"));
                domain.setTableComment(rs.getString("Comment"));
                
                list.add(domain);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return list;
    }
    
    public List<DorisExecShowData> execShowData(String catalogName, String databaseName, String tableName) {
        List<DorisExecShowData> list = new ArrayList<>();
        String sql;
        if (StringUtils.isEmpty(tableName)) {
            sql = "SHOW DATA ORDER BY TableName";
        } else {
            sql = "SHOW DATA FROM %s.%s.%s".formatted(catalogName, databaseName, tableName);
        }
        try (Connection connection = dorisJdbcTemplate.getDataSource().getConnection()) {
            connection.prepareStatement("use " + databaseName).execute();
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                if (StringUtils.isEmpty(rs.getString("TableName"))) {
                    continue;
                }
                DorisExecShowData domain = new DorisExecShowData();
                domain.setId(new DorisTableKey(databaseName, rs.getString("TableName")));
                domain.setSize(rs.getString("Size"));
                domain.setReplicaCount(rs.getInt("ReplicaCount"));
                domain.setRemoteSize(rs.getString("RemoteSize"));
                list.add(domain);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    
    public DorisShowCreateTable execShowCreateTable(String catalogName, String databaseName, String tableName) {
        String sql = "SHOW CREATE TABLE %s.%s".formatted(databaseName, tableName);
        return dorisJdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new DorisShowCreateTable()
                        .setId(new DorisTableKey(databaseName, tableName))
                        .setCreateTableScript(rs.getString("Create Table"))
        );
    }
    
    public List<DorisTable> execSelectTables(String catalogName, String databaseName, String tableName) {
        List<DorisTable> list = new ArrayList<>();
        
        String sql;
        if (StringUtils.isEmpty(tableName)) {
            sql = "SELECT * FROM information_schema.tables WHERE table_catalog = '%s' AND table_schema = '%s'".formatted(catalogName, databaseName);
        } else {
            sql = "SELECT * FROM information_schema.tables WHERE table_catalog = '%s' AND table_schema = '%s' AND table_name = '%s'".formatted(catalogName, databaseName, tableName);
        }
        try (Connection connection = dorisJdbcTemplate.getDataSource().getConnection()) {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                
                String createTime = rs.getString("create_time");
                String updateTime = rs.getString("update_time");
                
                LocalDateTime createDt = StringUtils.isNotEmpty(createTime) ? DateUtil.parse(createTime).toLocalDateTime() : null;
                LocalDateTime updateDt = StringUtils.isNotEmpty(updateTime) ? DateUtil.parse(updateTime).toLocalDateTime() : null;
                
                DorisTable domain = DorisTable.builder()
                                              .id(new DorisTableKey(rs.getString("table_schema"), rs.getString("table_name")))
                                              .comment(rs.getString("table_comment"))
                                              .tableType(DorisTableType.convert(rs.getString("TABLE_TYPE")))
                                              .rowNum(rs.getLong("table_rows"))
                                              .storageBytes(rs.getLong("data_length"))
                                              .avgRowBytes(rs.getLong("avg_row_length"))
                                              .createDt(createDt)
                                              .updateDt(updateDt)
                                              .build();
                list.add(domain);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return list;
    }
    
    public List<DorisColumn> execSelectColumns(String databaseName, String tableName) {
        List<DorisColumn> list = new ArrayList<>();
        
        String sql;
        if (StringUtils.isEmpty(tableName)) {
            sql = "SELECT * FROM information_schema.columns WHERE table_schema = '%s'".formatted(databaseName);
        } else {
            sql = "SELECT * FROM information_schema.columns WHERE table_schema = '%s' AND table_name = '%s'".formatted(databaseName, tableName);
        }
        try (Connection connection = dorisJdbcTemplate.getDataSource().getConnection()) {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                
                Long precision = rs.getLong("numeric_precision");
                if (rs.wasNull()) {
                    precision = rs.getLong("datetime_precision");
                }
                
                DorisColumn domain = DorisColumn.builder()
                                                .id(new DorisColumnKey(databaseName, rs.getString("table_name"), rs.getString("column_name")))
                                                .comment(rs.getString("column_comment"))
                                                .sort(rs.getLong("ordinal_position"))
                                                .columnType(rs.getString("column_key"))
                                                .dataTypeFormat(rs.getString("column_type"))
                                                .dataType(rs.getString("data_type"))
                                                .length(rs.getLong("character_maximum_length"))
                                                .precision(precision)
                                                .scale(rs.getLong("numeric_scale"))
                                                .notNull("NO".equals(rs.getString("is_nullable")))
                                                .extra(rs.getString("extra"))
                                                .defaultValue(rs.getString("column_default"))
                                                .build();
                list.add(domain);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return list;
    }
}