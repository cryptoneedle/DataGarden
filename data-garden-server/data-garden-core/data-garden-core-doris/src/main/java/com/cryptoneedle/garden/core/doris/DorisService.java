package com.cryptoneedle.garden.core.doris;

import com.cryptoneedle.garden.common.constants.CommonConstant;
import com.cryptoneedle.garden.common.enums.DorisTableModelType;
import com.cryptoneedle.garden.common.key.doris.DorisCatalogKey;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisDatabaseKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.common.util.DorisBucketUtil;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.doris.DorisMetadataRepository;
import com.cryptoneedle.garden.infrastructure.dto.DorisExecShowData;
import com.cryptoneedle.garden.infrastructure.entity.doris.*;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-14
 */
@Slf4j
@Service
public class DorisService {
    
    public final DorisService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    public final DorisMetadataRepository dorisMetadataRepository;
    
    
    public DorisService(@Lazy DorisService dorisService,
                        AddService addService,
                        SelectService selectService,
                        SaveService saveService,
                        DeleteService deleteService,
                        PatchService patchService,
                        DorisMetadataRepository dorisMetadataRepository) {
        this.service = dorisService;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
        this.dorisMetadataRepository = dorisMetadataRepository;
    }
    
    public void syncCatalog() {
        // 查询所有
        List<DorisCatalog> originList = select.doris.catalogs();
        
        // 待同步数据
        List<DorisCatalog> dealList = dorisMetadataRepository.showCatalogs()
                                                             .stream()
                                                             .map(deal ->
                                                                     new DorisCatalog()
                                                                             .setId(new DorisCatalogKey(deal.getCatalogName()))
                                                                             .setCatalogType(deal.getType())
                                                                             .setComment(deal.getComment())
                                                                             .setCreateDt(deal.getCreateTime())
                                                                             .setUpdateDt(deal.getLastUpdateTime())
                                                             )
                                                             .toList();
        
        Map<DorisCatalogKey, DorisCatalog> originMap = Maps.uniqueIndex(originList, DorisCatalog::getId);
        Map<DorisCatalogKey, DorisCatalog> dealMap = Maps.uniqueIndex(dealList, DorisCatalog::getId);
        
        // 新增数据
        List<DorisCatalog> addList = dealList.stream().filter(deal -> !originMap.containsKey(deal.getId())).toList();
        
        // 保存数据
        List<DorisCatalog> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisCatalog deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setCatalogType(deal.getCatalogType())
                              .setComment(deal.getComment())
                              .setCreateDt(deal.getCreateDt())
                              .setUpdateDt(deal.getUpdateDt());
                    }
                }).toList();
        
        // 移除数据
        List<DorisCatalog> removeList = originList.stream().filter(item -> !dealMap.containsKey(item.getId())).toList();
        
        add.doris.catalogs(addList);
        save.doris.catalogs(saveList);
        delete.doris.catalogs(removeList);
        
        service.syncDatabase(null, null);
    }
    
    public void syncDatabase(DorisDatabase database, DorisTable table) {
        // 查询所有
        List<DorisDatabase> originList = select.doris.databases();
        
        // 待同步数据
        List<DorisDatabase> dealList = dorisMetadataRepository.showDatabasesFrom(CommonConstant.DORIS_CATALOG)
                                                              .stream()
                                                              .filter(deal -> !Strings.CS.equalsAny(deal.getDatabaseName(), "__internal_schema", "information_schema", "mysql"))
                                                              .map(deal ->
                                                                      new DorisDatabase().setId(new DorisDatabaseKey(deal.getDatabaseName()))
                                                              )
                                                              .toList();
        
        Map<DorisDatabaseKey, DorisDatabase> originMap = Maps.uniqueIndex(originList, DorisDatabase::getId);
        Map<DorisDatabaseKey, DorisDatabase> dealMap = Maps.uniqueIndex(dealList, DorisDatabase::getId);
        
        // 新增数据
        List<DorisDatabase> addList = dealList.stream().filter(deal -> !originMap.containsKey(deal.getId())).toList();
        
        // 移除数据
        List<DorisDatabase> removeList = originList.stream().filter(item -> !dealMap.containsKey(item.getId())).toList();
        
        // 保存数据
        List<DorisDatabase> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisDatabase deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setId(deal.getId());
                    }
                }).toList();
        
        add.doris.databases(addList);
        save.doris.databases(saveList);
        delete.doris.databases(removeList);
        
        service.syncTable(database, table);
    }
    
    public void syncTable(DorisDatabase database, DorisTable table) {
        String databaseName = database.getId().getDatabaseName();
        String tableName = table != null ? table.getId().getTableName() : null;
        
        List<DorisExecShowData> dorisExecShowDatas = dorisMetadataRepository.execShowData(CommonConstant.DORIS_CATALOG, databaseName, tableName);
        Map<DorisTableKey, DorisExecShowData> dorisExecShowDataMap = Maps.uniqueIndex(dorisExecShowDatas, DorisExecShowData::getId);
        
        
        List<DorisTable> dealList = dorisMetadataRepository.execSelectTables(CommonConstant.DORIS_CATALOG, databaseName, tableName);
        for (DorisTable dorisTable : dealList) {
            DorisTableKey id = dorisTable.getId();
            
            // 初始化变量，不作为最终结果
            dorisTable.setPartitioned(false);
            
            DorisExecShowData dorisExecShowData = dorisExecShowDataMap.get(id);
            if (dorisExecShowData != null) {
                dorisTable.setStorageSpaceFormat(dorisExecShowData.getSize())
                          .setReplicaCount(dorisExecShowData.getReplicaCount())
                          .setRemoteSize(dorisExecShowData.getRemoteSize());
            }
            
            // todo columnNum
        }
        
        // 查询所有
        List<DorisTable> originList;
        if (StringUtils.isEmpty(tableName)) {
            originList = select.doris.tables(databaseName);
        } else {
            originList = List.of(select.doris.table(new DorisTableKey(databaseName, tableName)));
        }
        
        Map<DorisTableKey, DorisTable> originMap = Maps.uniqueIndex(originList, DorisTable::getId);
        Map<DorisTableKey, DorisTable> dealMap = Maps.uniqueIndex(dealList, DorisTable::getId);
        
        // 新增数据
        List<DorisTable> addList = dealList.stream()
                                           .filter(deal -> !originMap.containsKey(deal.getId()))
                                           .peek(this::syncTableWithCreateTableField)
                                           .toList();
        
        // 移除数据
        List<DorisTable> removeList = originList.stream()
                                                .filter(item -> !dealMap.containsKey(item.getId()))
                                                .toList();
        
        // 保存数据
        List<DorisTable> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisTable deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        if (!Objects.equals(deal.getCreateDt(), origin.getCreateDt()) || !Objects.equals(deal.getUpdateDt(), origin.getUpdateDt())) {
                            syncTableWithCreateTableField(origin);
                        }
                        origin.setTableType(deal.getTableType())
                              .setComment(deal.getComment())
                              .setRowNum(deal.getRowNum())
                              .setStorageBytes(deal.getStorageBytes())
                              .setAvgRowBytes(deal.getAvgRowBytes())
                              .setCreateDt(deal.getCreateDt())
                              .setUpdateDt(deal.getUpdateDt())
                              .setStorageSpaceFormat(deal.getStorageSpaceFormat())
                              .setReplicaCount(deal.getReplicaCount())
                              .setRemoteSize(deal.getRemoteSize());
                    }
                }).toList();
        
        add.doris.tables(addList);
        save.doris.tables(saveList);
        delete.doris.tables(removeList);
        
        service.syncColiumn(database, table);
    }
    
    public void syncTableWithCreateTableField(DorisTable dorisTable) {
        DorisShowCreateTable dorisShowCreateTable = dorisMetadataRepository.execShowCreateTable(CommonConstant.DORIS_CATALOG, dorisTable.getId()
                                                                                                                                        .getDatabaseName(), dorisTable.getId()
                                                                                                                                                                      .getTableName());
        if (dorisShowCreateTable != null) {
            String createTableScript = dorisShowCreateTable.getCreateTableScript();
            dorisTable.setCreateTableScript(createTableScript);
            
            if (Strings.CS.contains(createTableScript, "UNIQUE KEY(")) {
                dorisTable.setTableModelType(DorisTableModelType.UNIQUE_KEY);
            } else if (Strings.CS.contains(createTableScript, "AGGREGATE KEY(")) {
                dorisTable.setTableModelType(DorisTableModelType.AGGREGATE_KEY);
            } else if (Strings.CS.contains(createTableScript, "DUPLICATE KEY(")) {
                dorisTable.setTableModelType(DorisTableModelType.DUPLICATE_KEY);
            } else {
                log.error("Unknown Table Type: " + createTableScript);
            }
            
            if (Strings.CS.contains(createTableScript, "PARTITION BY")) {
                dorisTable.setPartitioned(true);
            }
            
            Matcher matcher = Pattern.compile("BUCKETS\\s+([\\w]+)", Pattern.CASE_INSENSITIVE).matcher(createTableScript);
            dorisTable.setBucketNum(matcher.find() ? matcher.group(1) : null);
            
            Long storageBytes = dorisTable.getStorageBytes();
            if (storageBytes != null) {
                dorisTable.setEstimateBucketNum(DorisBucketUtil.estimateBucket(storageBytes, 1.0));
            }
        }
    }
    
    public void syncColiumn(DorisDatabase database, DorisTable table) {
        String databaseName = database.getId().getDatabaseName();
        String tableName = table != null ? table.getId().getTableName() : null;
        
        // 待同步数据
        List<DorisColumn> dealList = dorisMetadataRepository.execSelectColumns(databaseName, tableName);
        
        // 查询所有
        List<DorisColumn> originList;
        if (StringUtils.isEmpty(tableName)) {
            originList = select.doris.columns(databaseName);
        } else {
            originList = select.doris.columns(databaseName, tableName);
        }
        
        Map<DorisColumnKey, DorisColumn> originMap = Maps.uniqueIndex(originList, DorisColumn::getId);
        Map<DorisColumnKey, DorisColumn> dealMap = Maps.uniqueIndex(dealList, DorisColumn::getId);
        
        // 新增数据
        List<DorisColumn> addList = dealList.stream().filter(deal -> !originMap.containsKey(deal.getId())).toList();
        
        // 移除数据
        List<DorisColumn> removeList = originList.stream().filter(item -> !dealMap.containsKey(item.getId())).toList();
        
        // 保存数据
        List<DorisColumn> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisColumn deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setComment(deal.getComment())
                              .setSort(deal.getSort())
                              .setColumnType(deal.getColumnType())
                              .setDataTypeFormat(deal.getDataTypeFormat())
                              .setDataType(deal.getDataType())
                              .setLength(deal.getLength())
                              .setPrecision(deal.getPrecision())
                              .setScale(deal.getScale())
                              .setNotNull(deal.getNotNull())
                              .setExtra(deal.getExtra())
                              .setDefaultValue(deal.getDefaultValue());
                    }
                }).toList();
        
        add.doris.columns(addList);
        save.doris.columns(saveList);
        delete.doris.columns(removeList);
    }
}