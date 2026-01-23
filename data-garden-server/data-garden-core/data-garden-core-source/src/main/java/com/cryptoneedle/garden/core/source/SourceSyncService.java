package com.cryptoneedle.garden.core.source;

import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.common.key.source.SourceDimensionColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.spi.DataSourceExecutor;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-21
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SourceSyncService {
    
    public final SourceSyncService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    public final SourceSyncService sync;
    public final SourceTransformService transform;
    
    
    public SourceSyncService(@Lazy SourceSyncService sourceSyncService,
                             AddService addService,
                             SelectService selectService,
                             SaveService saveService,
                             DeleteService deleteService,
                             PatchService patchService,
                             SourceTransformService sourceTransformService) {
        this.service = sourceSyncService;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
        this.sync = sourceSyncService;
        this.transform = sourceTransformService;
    }
    
    @Async("asyncExecutor")
    @Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void syncCatalog(SourceCatalog catalog, boolean onlyDatabase) {
        log.info("[sync] Catalog 开始: {}", catalog.getId().getCatalogName());
        try {
            service.syncDatabase(catalog, null, onlyDatabase);
            log.info("[sync] Catalog 完成: {}", catalog.getId().getCatalogName());
        } catch (Exception e) {
            log.error("[sync] Catalog 失败: {}", catalog.getId().getCatalogName(), e);
            throw e;
        }
    }
    
    @Async("asyncExecutor")
    @Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void syncDatabase(SourceCatalog catalog, SourceDatabase database, boolean onlyDatabase) {
        log.info("[sync] Database 开始: catalog={}, database={}", 
                catalog.getId().getCatalogName(), 
                database != null ? database.getId().getDatabaseName() : "all");
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
                                                     deal.setStatisticDt(localDateTime);
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
        
        patch.source.databaseDefault(catalog);
        
        if (!onlyDatabase) {
            service.syncTable(catalog, database, null);
        }
        
        log.info("[sync] Database 完成: catalog={}, database={}", 
                catalog.getId().getCatalogName(), 
                database != null ? database.getId().getDatabaseName() : "all");
    }
    
    @Async("asyncExecutor")
    @Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void syncTable(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        log.info("[sync] Table 开始: catalog={}, database={}, table={}", 
                catalog.getId().getCatalogName(),
                database != null ? database.getId().getDatabaseName() : "all",
                table != null ? table.getId().getTableName() : "all");
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
                                              .peek(deal -> {})
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
        
        patch.source.tableDefault(catalog);
        
        service.syncColumn(catalog, database, table);
        service.syncDimension(catalog, database, table);
        
        transform.transTable(catalog, database, table);
        
        log.info("[sync] Table 完成: catalog={}, database={}, table={}", 
                catalog.getId().getCatalogName(),
                database != null ? database.getId().getDatabaseName() : "all",
                table != null ? table.getId().getTableName() : "all");
    }
    
    public void syncColumn(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        log.info("[sync] Column");
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
                                               .peek(deal -> {
                                               })
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
        log.info("[sync] Dimension");
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
        originList = originList.stream().filter(origin -> !origin.getId().getDimensionType().equals(SourceDimensionType.MANUAL)).toList();
        
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