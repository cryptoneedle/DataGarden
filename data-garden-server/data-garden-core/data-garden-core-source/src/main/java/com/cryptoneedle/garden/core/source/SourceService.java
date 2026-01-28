package com.cryptoneedle.garden.core.source;

import cn.hutool.v7.socket.SocketUtil;
import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceDimensionColumnKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.doris.DorisMetadataRepository;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceCatalogSaveVo;
import com.cryptoneedle.garden.spi.DataSourceExecutor;
import com.cryptoneedle.garden.spi.DataSourceManager;
import com.cryptoneedle.garden.spi.DataSourceProvider;
import com.cryptoneedle.garden.spi.DataSourceSpiLoader;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>description: 配置-数据源目录-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-16
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SourceService {
    
    public final SourceService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    public final DorisMetadataRepository dorisMetadataRepository;
    public final SourceSyncService sync;
    
    public SourceService(@Lazy SourceService sourceService,
                         AddService addService,
                         SelectService selectService,
                         SaveService saveService,
                         DeleteService deleteService,
                         PatchService patchService,
                         DorisMetadataRepository dorisMetadataRepository,
                         SourceSyncService sourceSyncService) {
        this.service = sourceService;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
        this.dorisMetadataRepository = dorisMetadataRepository;
        this.sync = sourceSyncService;
    }
    
    public void fillPassword(@Valid SourceCatalogSaveVo vo) {
        if (StringUtils.isEmpty(vo.getPassword())) {
            SourceCatalog old = select.source.catalog(vo.sourceCatalogKey());
            if (old != null) {
                vo.setPassword(old.getPassword());
            }
        }
    }
    
    public boolean testServer(SourceCatalog catalog, boolean needStore) {
        boolean connected = false;
        try (Socket socket = SocketUtil.connect(catalog.getHost(), catalog.getPort())) {
            connected = socket.isConnected();
            
            // 持久化
            if (needStore) {
                select.source.catalogCheck(catalog.getId());
                catalog.setServerConnected(connected);
                if (connected) {
                    catalog.setServerConnectedDt(LocalDateTime.now());
                }
                save.source.catalog(catalog);
            }
        } catch (Exception e) {
            log.warn("Test connection failed", e);
        }
        return connected;
    }
    
    public boolean testJdbc(SourceCatalog catalog, boolean needStore) {
        boolean connected = false;
        try {
            DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
            if (provider != null) {
                String url = provider.buildJdbcUrl(catalog);
            }
            connected = DataSourceManager.testConnection(catalog);
            // 持久化
            if (needStore) {
                select.source.catalogCheck(catalog.getId());
                catalog.setJdbcConnected(connected);
                if (connected) {
                    catalog.setJdbcConnectedDt(LocalDateTime.now());
                }
                save.source.catalog(catalog);
            }
        } catch (Exception e) {
            log.warn("Test connection failed", e);
        }
        return connected;
    }
    
    public String testJdbcVersion(SourceCatalog catalog, boolean needStore) {
        String version = DataSourceExecutor.version(catalog);
        
        try {
            if (needStore) {
                select.source.catalogCheck(catalog.getId());
                catalog.setVersion(version);
                save.source.catalog(catalog);
            }
        } catch (Exception e) {
            log.warn("Test jdbc version failed", e);
        }
        return version;
    }
    
    public boolean testDoris(SourceCatalog catalog, boolean needStore) {
        boolean connected = false;
        try {
            try {
                dorisMetadataRepository.execDropCatalogIfExists(catalog, true);
                connected = service.execCreateCatalog(catalog, true);
                dorisMetadataRepository.execDropCatalog(catalog, true);
                connected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // 持久化
            if (needStore) {
                select.source.catalogCheck(catalog.getId());
                catalog.setDorisConnected(connected);
                if (connected) {
                    catalog.setDorisConnectedDt(LocalDateTime.now());
                }
                save.source.catalog(catalog);
            }
        } catch (Exception e) {
            log.warn("Test doris failed", e);
        }
        return connected;
    }
    
    public boolean execCreateCatalog(SourceCatalog catalog, boolean needTest) {
        String createCatalog = createCatalogSql(catalog, needTest);
        return dorisMetadataRepository.execReturnBoolean(createCatalog);
    }
    
    public String createCatalogSql(SourceCatalog catalog, boolean needTest) {
        String catalogPrefix = needTest ? "doris_test_" : "";
        String dorisCatalog = catalogPrefix + catalog.getDorisCatalog();
        
        return String.format("""
                        CREATE CATALOG IF NOT EXISTS `%s` COMMENT '%s'
                        PROPERTIES (
                            "type"="jdbc",
                            "driver_url" = "%s",
                            "driver_class" = "%s",
                            "jdbc_url" = "%s",
                            "user"="%s",
                            "password"="%s",
                            "lower_case_meta_names" = "false",
                            "meta_names_mapping" = "",
                            "only_specified_database" = "false",
                            "include_database_list" = "",
                            "exclude_database_list" = "",
                            "connection_pool_min_size" = "%s",
                            "connection_pool_max_size" = "%s",
                            "connection_pool_max_wait_time" = "%s",
                            "connection_pool_max_life_time" = "%s",
                            "connection_pool_keep_alive" = "%s",
                            "enable.auto.analyze" ="false"
                        );"""
                , dorisCatalog
                , catalog.getId().getCatalogName()
                , select.config.dorisCatalogDriverUrl(catalog.getDatabaseType())
                , select.config.dorisCatalogDriverClass(catalog.getDatabaseType())
                , DataSourceSpiLoader.getProvider(catalog.getDatabaseType()).buildJdbcUrl(catalog)
                , catalog.getUsername()
                , catalog.getPassword()
                , select.config.dorisCatalogConnectionPoolMinSize()
                , select.config.dorisCatalogConnectionPoolMaxSize()
                , select.config.dorisCatalogConnectionPoolMaxWaitTime()
                , select.config.dorisCatalogConnectionPoolMaxLifeTime()
                , select.config.dorisCatalogConnectionPoolKeepAlive()
        );
    }
    
    public void addVo(SourceCatalogSaveVo vo) {
        SourceCatalog catalog = add.source.catalog(vo);
        // 测试
        service.testServer(catalog, true);
        service.testJdbc(catalog, true);
        service.testJdbcVersion(catalog, true);
        //service.testDoris(catalog, true);
    }
    
    public void saveVo(SourceCatalogSaveVo vo) {
        SourceCatalog catalog = save.source.catalog(vo);
        // 测试
        service.testServer(catalog, true);
        service.testJdbc(catalog, true);
        service.testJdbcVersion(catalog, true);
        //service.testDoris(catalog, true);
    }
    
    public String createDorisTableScript(SourceTable table) {
        // "skip_write_index_on_load" = "true",
        // "enable_unique_key_skip_bitmap_column" = "true",
        // "disable_storage_row_cache" = "true"
        
        String databaseName = select.config.dorisSchemaOds();
        String tableName = table.getTransTableName();
        String columnDefinition = columnDefinitions(table);
        String uniqueKeys = uniqueKeys(table);
        String bucket = table.getTransBucketNum();
        String comment = table.getTransComment();
        String replicationNum = select.config.dorisConfigReplicationNum();
        
        return """
                CREATE TABLE IF NOT EXISTS %s.%s(
                %s
                    `gather_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间'
                ) UNIQUE KEY(%s)
                COMMENT '%s'
                DISTRIBUTED BY HASH(%s) BUCKETS %s
                PROPERTIES (
                    "replication_num" = "%s",
                    "is_being_synced" = "false",
                    "compression" = "LZ4",
                    "enable_unique_key_merge_on_write" = "true",
                    "light_schema_change" = "true",
                    "enable_mow_light_delete" = "false",
                    "store_row_column" = "true"
                );""".formatted(databaseName, tableName, columnDefinition, uniqueKeys, comment, uniqueKeys, bucket, replicationNum);
    }
    
    public Object createDorisTableScriptBatch(String catalogName, String databaseName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SourceTable table : select.source.tablesEnabled(catalogName, databaseName)) {
            stringBuilder.append(service.createDorisTableScript(table));
            stringBuilder.append("\n\n");
        }
        return stringBuilder;
    }
    
    public String uniqueKeys(SourceTable table) {
        return select.source.columnsWithDimension(table).stream().map(column -> "`" + column.getTransColumnName() + "`").collect(Collectors.joining(", "));
    }
    
    public String columnDefinitions(SourceTable table) {
        // 维度列（需要优先级）
        List<SourceColumn> dimensions = select.source.columnsWithDimension(table);
        // 非维度列
        List<SourceColumn> columns = select.source.columnsWithoutDimension(table);
        
        // 列总量
        int columnCount = dimensions.size() + columns.size();
        
        // 构造列表
        List<String> columnNames = new ArrayList<>(columnCount);
        List<String> columnTypes = new ArrayList<>(columnCount);
        List<String> columnComments = new ArrayList<>(columnCount);
        for (SourceColumn column : dimensions) {
            columnNames.add("`" + column.getTransColumnName() + "`");
            columnTypes.add(column.getTransDataTypeFormat());
            columnComments.add(column.getTransComment());
        }
        for (SourceColumn column : columns) {
            columnNames.add("`" + column.getTransColumnName() + "`");
            columnTypes.add(column.getTransDataTypeFormat());
            columnComments.add(column.getTransComment());
        }
        
        // 计算最大长度（用于格式化）
        int maxColumnName = columnNames.stream().mapToInt(String::length).max().orElse(0);
        int maxColumnType = columnTypes.stream().mapToInt(String::length).max().orElse(0);
        
        StringBuilder columnDefinition = new StringBuilder();
        for (int i = 0; i < columnCount; i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            String columnComment = columnComments.get(i);
            if (i < columnCount - 1) {
                columnDefinition.append(("    %-" + maxColumnName + "s %-" + maxColumnType + "s COMMENT '%s',\n").formatted(columnName, columnType, columnComment));
            } else {
                columnDefinition.append(("    %-" + maxColumnName + "s %-" + maxColumnType + "s COMMENT '%s',").formatted(columnName, columnType, columnComment));
            }
        }
        return columnDefinition.toString();
    }
    
    public void saveDimensions(String catalogName, String databaseName, String tableName, List<String> columnNames) throws EntityNotFoundException {
        String dimensionName = "CK_" + tableName;
        List<SourceDimension> dimensions = select.source.dimensions(catalogName, databaseName, tableName, SourceDimensionType.MANUAL, dimensionName);
        if (!dimensions.isEmpty()) {
            delete.source.dimensions(dimensions);
        }
        long sort = 0;
        for (String columnName : columnNames) {
            select.source.columnCheck(new SourceColumnKey(catalogName, databaseName, tableName, columnName));
            // 创建新维度
            SourceDimension dimension = SourceDimension.builder()
                                                       .id(new SourceDimensionColumnKey(catalogName, databaseName, tableName, SourceDimensionType.MANUAL, dimensionName, columnName))
                                                       .sort(sort++)
                                                       .enabled(false)
                                                       .build();
            
            save.source.dimension(dimension);
        }
    }
    
    public void fillConfigSsh(@Valid SourceCatalogSaveVo vo) {
        if (!StringUtils.isEmpty(vo.getSshHost())) {
            ConfigSsh ssh = select.config.ssh(vo.getSshHost());
            if (ssh != null) {
                vo.setConfigSsh(ssh);
            }
        }
    }
    
    public String createSeatunnelScript(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        StringBuilder script = new StringBuilder();
        
        // env
        script.append("""
                env {
                  job.mode = "BATCH"
                  #shade.identifier = "base64"
                }
                
                """);
        
        // Source
        script.append("""
                source {
                  Jdbc {
                    result_table_name = "source_data"
                    url = "%s"
                    driver = %s
                    user = "%s"
                    password = "%s"
                    connection_check_timeout_sec = 100000
                    fetch_size = 5000
                    query = \"""SELECT %s FROM %s.%s\"""
                    #where_condition = "WHERE TRUE"
                  }
                }
                
                """.formatted(catalog.getUrl(),
                select.config.dorisCatalogDriverClass(catalog.getDatabaseType()),
                catalog.getUsername(),
                catalog.getPassword(),
                service.selectConvertColumnAs(catalog, database, table),
                table.getId().getDatabaseName(),
                table.getId().getTableName()));
        
        // Sink
        script.append("""
                sink {
                   Doris {
                    source_table_name = "source_data"
                    fenodes = "%s:%s"
                    username = "%s"
                    password = "%s"
                    database = %s
                    doris.batch.size = 20000
                    data_save_mode = "APPEND_DATA"
                    table = "%s"
                    sink.label-prefix = "%s"
                    sink.enable-2pc = "false"
                    doris.config {
                      format = "json"
                      read_json_by_line = "true"
                    }
                  }
                }
                """.formatted(select.config.dorisDatasourceFeHost(),
                select.config.dorisDatasourceFeStreamLoadPort(),
                select.config.dorisDatasourceUsername(),
                select.config.dorisDatasourcePassword(),
                select.config.dorisSchemaOds(),
                table.getTransTableName(),
                table.getTransTableName()));
        
        return script.toString();
    }
    
    public String selectConvertColumnAs(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
        String delimiter = provider.identifierDelimiter();
        List<SourceColumn> columns = select.source.columns(table.getId().getCatalogName(), table.getId().getDatabaseName(), table.getId().getTableName());
        return columns.stream()
                      // eg. NAME -> name => (Oracle) "NAME" AS "name"
                      .map(column -> delimiter + column.getId()
                                                       .getColumnName() + delimiter + " AS " + delimiter + column.getTransColumnName() + delimiter)
                      .collect(Collectors.joining(","));
    }
}