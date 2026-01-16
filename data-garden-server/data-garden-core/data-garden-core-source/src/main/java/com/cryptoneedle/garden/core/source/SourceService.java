package com.cryptoneedle.garden.core.source;

import cn.hutool.v7.socket.SocketUtil;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.common.key.source.SourceDimensionColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceCatalogSaveVo;
import com.cryptoneedle.garden.spi.DataSourceExecutor;
import com.cryptoneedle.garden.spi.DataSourceManager;
import com.cryptoneedle.garden.spi.DataSourceProvider;
import com.cryptoneedle.garden.spi.DataSourceSpiLoader;
import com.google.common.collect.Maps;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    
    public SourceService(@Lazy SourceService sourceService,
                         AddService addService,
                         SelectService selectService,
                         SaveService saveService,
                         DeleteService deleteService,
                         PatchService patchService) {
        this.service = sourceService;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
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
                catalog.setUrl(url);
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
        return true;
    }
    
    public void addVo(SourceCatalogSaveVo vo) {
        SourceCatalog catalog = add.source.catalog(vo);
        // 测试
        service.testServer(catalog, true);
        service.testJdbc(catalog, true);
        service.testJdbcVersion(catalog, true);
        service.testDoris(catalog, true);
    }
    
    public void saveVo(SourceCatalogSaveVo vo) {
        SourceCatalog catalog = save.source.catalog(vo);
        // 测试
        service.testServer(catalog, true);
        service.testJdbc(catalog, true);
        service.testJdbcVersion(catalog, true);
        service.testDoris(catalog, true);
    }
    
    public void syncCatalog(SourceCatalog catalog) {
        service.syncDatabase(catalog, null);
    }
    
    public void syncDatabase(SourceCatalog catalog, SourceDatabase database) {
        LocalDateTime localDateTime = LocalDateTime.now();
        List<SourceDatabase> originList;
        List<SourceDatabase> dealList = DataSourceExecutor.databases(catalog, database);
        if (database == null) {
            originList = select.source.databases(catalog.getId().getCatalogName());
        } else {
            originList = List.of(select.source.database(database.getId()));
        }
        
        // 已存在
        Map<SourceDatabaseKey, SourceDatabase> originMap = Maps.uniqueIndex(originList, SourceDatabase::getId);
        // 待处理
        Map<SourceDatabaseKey, SourceDatabase> dealMap = Maps.uniqueIndex(dealList, SourceDatabase::getId);
        
        // 新增
        List<SourceDatabase> extraList = dealList.stream()
                                                 .filter(deal -> !originMap.containsKey(deal.getId()))
                                                 .filter(deal -> deal.getTotalNum() > 0)
                                                 .peek(deal -> {
                                                     deal.setDorisCatalogName(catalog.getDorisCatalogName())
                                                         .setSystemCode(catalog.getSystemCode())
                                                         .setCollectFrequency(catalog.getCollectFrequency())
                                                         .setCollectTimePoint(catalog.getCollectTimePoint())
                                                         .setStatisticDt(localDateTime);
                                                 })
                                                 .toList();
        
        // 保存
        List<SourceDatabase> existsList = originList.stream()
                                                    .filter(origin -> dealMap.containsKey(origin.getId()))
                                                    .peek(origin -> {
                                                        SourceDatabase deal = dealMap.get(origin.getId());
                                                        if (deal != null) {
                                                            origin.setTotalNum(deal.getTotalNum())
                                                                  .setTableNum(deal.getTableNum())
                                                                  .setViewNum(deal.getViewNum())
                                                                  .setMaterializedViewNum(deal.getMaterializedViewNum())
                                                                  .setStatisticDt(localDateTime);
                                                        }
                                                    }).toList();
        
        // 移除
        List<SourceDatabase> missList = originList.stream()
                                                  .filter(item -> !dealMap.containsKey(item.getId()))
                                                  .toList();
        
        add.source.databases(extraList);
        save.source.databases(existsList);
        delete.source.databases(missList);
        
        service.syncTable(catalog, database, null);
    }
    
    public void syncTable(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        String databaseName = database != null ? database.getId().getDatabaseName() : null;
        String tableName = table != null ? table.getId().getTableName() : null;
        
        List<SourceTable> originList;
        if (database == null) {
            originList = select.source.tables(catalog.getId().getCatalogName());
        } else {
            if (table == null) {
                originList = select.source.tables(database.getId().getCatalogName(), databaseName);
            } else {
                originList = List.of(select.source.table(table.getId()));
            }
        }
        List<SourceTable> dealList = DataSourceExecutor.tables(catalog, databaseName, tableName);
        // 已存在
        Map<SourceTableKey, SourceTable> originMap = Maps.uniqueIndex(originList, SourceTable::getId);
        // 待处理
        Map<SourceTableKey, SourceTable> dealMap = Maps.uniqueIndex(dealList, SourceTable::getId);
        
        // 新增
        List<SourceTable> extraList = dealList.stream()
                                              .filter(deal -> !originMap.containsKey(deal.getId()))
                                              .peek(deal -> {
                                              })
                                              .toList();
        
        // 保存
        List<SourceTable> existsList = originList.stream()
                                                 .filter(origin -> dealMap.containsKey(origin.getId()))
                                                 .peek(origin -> {
                                                     SourceTable deal = dealMap.get(origin.getId());
                                                     if (deal != null) {
                                                         origin.setComment(deal.getComment())
                                                               .setTableType(deal.getTableType())
                                                               .setRowNum(deal.getRowNum())
                                                               .setAvgRowBytes(deal.getAvgRowBytes())
                                                               .setStatisticDt(deal.getStatisticDt());
                                                     }
                                                 }).toList();
        
        // 移除
        List<SourceTable> missList = originList.stream().filter(item -> !dealMap.containsKey(item.getId())).toList();
        
        add.source.tables(extraList);
        save.source.tables(existsList);
        delete.source.tables(missList);
        
        service.syncColumn(catalog, database, table);
        service.syncDimension(catalog, database, table);
    }
    
    public void syncColumn(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        String databaseName = database != null ? database.getId().getDatabaseName() : null;
        String tableName = table != null ? table.getId().getTableName() : null;
        
        List<SourceColumn> originList;
        if (database == null) {
            originList = select.source.columns(catalog.getId().getCatalogName());
        } else {
            if (table == null) {
                originList = select.source.columns(database.getId().getCatalogName(), databaseName);
            } else {
                originList = select.source.columns(database.getId().getCatalogName(), databaseName, tableName);
            }
        }
        List<SourceColumn> dealList = DataSourceExecutor.columns(catalog, databaseName, tableName);
        
        // 已存在
        Map<SourceColumnKey, SourceColumn> originMap = Maps.uniqueIndex(originList, SourceColumn::getId);
        // 待处理
        Map<SourceColumnKey, SourceColumn> dealMap = Maps.uniqueIndex(dealList, SourceColumn::getId);
        
        // 新增
        List<SourceColumn> extraList = dealList.stream()
                                               .filter(deal -> !originMap.containsKey(deal.getId()))
                                               .peek(deal -> {})
                                               .toList();
        
        // 保存
        List<SourceColumn> existsList = originList.stream()
                                                  .filter(origin -> dealMap.containsKey(origin.getId()))
                                                  .peek(origin -> {
                                                      SourceColumn deal = dealMap.get(origin.getId());
                                                      if (deal != null) {
                                                          origin.setComment(deal.getComment())
                                                                .setSort(deal.getSort())
                                                                .setDataType(deal.getDataType())
                                                                .setLength(deal.getLength())
                                                                .setPrecision(deal.getPrecision())
                                                                .setScale(deal.getScale())
                                                                .setNotNull(deal.getNotNull())
                                                                .setSampleNum(deal.getSampleNum())
                                                                .setNullNum(deal.getNullNum())
                                                                .setDistinctNum(deal.getDistinctNum())
                                                                .setDensity(deal.getDensity())
                                                                .setMinValue(deal.getMinValue())
                                                                .setMaxValue(deal.getMaxValue())
                                                                .setAvgColumnBytes(deal.getAvgColumnBytes())
                                                                .setStatisticDt(deal.getStatisticDt());
                                                      }
                                                  }).toList();
        
        // 移除
        List<SourceColumn> missList = originList.stream().filter(item -> !dealMap.containsKey(item.getId())).toList();
        
        add.source.columns(extraList);
        save.source.columns(existsList);
        delete.source.columns(missList);
    }
    
    public void syncDimension(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        String databaseName = database != null ? database.getId().getDatabaseName() : null;
        String tableName = table != null ? table.getId().getTableName() : null;
        
        List<SourceDimension> originList;
        if (database == null) {
            originList = select.source.dimensions(catalog.getId().getCatalogName());
        } else {
            if (table == null) {
                originList = select.source.dimensions(database.getId().getCatalogName(), databaseName);
            } else {
                originList = select.source.dimensions(database.getId().getCatalogName(), databaseName, tableName);
            }
        }
        List<SourceDimension> dealList = DataSourceExecutor.dimensions(catalog, databaseName, tableName);
        
        // 已存在
        Map<SourceDimensionColumnKey, SourceDimension> originMap = Maps.uniqueIndex(originList, SourceDimension::getId);
        // 待处理
        Map<SourceDimensionColumnKey, SourceDimension> dealMap = Maps.uniqueIndex(dealList, SourceDimension::getId);
        
        // 新增
        List<SourceDimension> extraList = dealList.stream()
                                                  .filter(deal -> !originMap.containsKey(deal.getId()))
                                                  .peek(deal -> {})
                                                  .toList();
        
        // 保存
        List<SourceDimension> existsList = originList.stream()
                                                     .filter(origin -> dealMap.containsKey(origin.getId()))
                                                     .peek(origin -> {
                                                         SourceDimension deal = dealMap.get(origin.getId());
                                                         if (deal != null) {
                                                             origin.setSort(deal.getSort());
                                                         }
                                                     }).toList();
        
        // 移除
        List<SourceDimension> missList = originList.stream().filter(item -> !dealMap.containsKey(item.getId())).toList();
        
        add.source.dimensions(extraList);
        save.source.dimensions(existsList);
        delete.source.dimensions(missList);
    }
}