package com.cryptoneedle.garden.core.doris;

import com.cryptoneedle.garden.common.constants.CommonConstant;
import com.cryptoneedle.garden.common.enums.DorisTableModelType;
import com.cryptoneedle.garden.common.enums.DorisTableType;
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
        
        for (DorisDatabase dorisDatabase : select.doris.databases()) {
            service.syncTable(dorisDatabase, table);
        }
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
        if (!DorisTableType.BASE_TABLE.equals(dorisTable.getTableType())) {
            return;
        }
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
    
    public void fixCreateTable() {
        // todo 从配置中提取
        String[] databases = new String[]{"seatone_ads", "seatone_dim", "seatone_dwd", "seatone_dwd_standard", "seatone_dws", "seatone_mapping", "seatone_mapping_standard", "seatone_ods", "seatone_pre"};
        List<DorisTable> dorisTables = select.doris.tables()
                                             .stream()
                                             .filter(table -> Strings.CI.containsAny(table.getId().getDatabaseName(), databases))
                                             .toList();
        int tableMaxLength = dorisTables.stream()
                                        .map(table -> table.getId().getDatabaseName() + "." + table.getId().getTableName())
                                        .mapToInt(String::length)
                                        .max()
                                        .orElse(0);
        
        int tableNum = dorisTables.size();
        for (int i = 0; i < tableNum; i++) {
            DorisTable dorisTable = dorisTables.get(i);
            String tableName = dorisTable.getId().getTableName();
            String databaseName = dorisTable.getId().getDatabaseName();
            String bucketNum = dorisTable.getBucketNum();
            Integer estimateBucket = dorisTable.getEstimateBucketNum();
            DorisTableModelType tableModel = dorisTable.getTableModelType();
            
            String createTableSql = dorisTable.getCreateTableScript();
            
            String comment = ("-- [%-4s/%-4s] %-" + tableMaxLength + "s | Storage(include 3 copies): %-9s Mb | ReplicaCount: %-3s | Bucket: %-4s | EstimateBucket: %-2s")
                    .formatted(i + 1, tableNum, databaseName + "." + tableName, dorisTable.getStorageSpaceFormat(), dorisTable.getReplicaCount(), bucketNum, estimateBucket);
            log.info(comment);
            
            // 不进行处理的情况
            if (!DorisTableType.BASE_TABLE.equals(dorisTable.getTableType())) {
                log.warn("Skip Without Table: {}", dorisTable.getTableType());
                continue;
            }
            if (DorisTableModelType.AGGREGATE_KEY.equals(tableModel)) {
                log.warn("Skip AGGREGATE KEY Table: {}", tableName);
                continue;
            }
            if (DorisTableModelType.DUPLICATE_KEY.equals(tableModel)) {
                log.warn("Skip DUPLICATE KEY Table: {}", tableName);
                continue;
            }
            if (dorisTable.getPartitioned()) {
                log.warn("Skip PARTITION BY Table: {}", tableName);
                continue;
            }
            if (estimateBucket == null) {
                log.warn("Skip EstimateBucket NULL Table: {}", tableName);
                continue;
            }
            
            String bucket = estimateBucket.toString();
            if (Strings.CI.equals(bucketNum, bucket)) {
                //log.warn("Skip Equal EstimateBucket Table: {}", tableName);
                continue;
            }
            
            // 临时表名 fix_xxx
            String newTableName = "fix_" + tableName;
            String newCreateTableSql = Strings.CS.replace(createTableSql, "CREATE TABLE", "CREATE TABLE IF NOT EXISTS");
            newCreateTableSql = Strings.CS.replace(newCreateTableSql, "`%s`".formatted(tableName), "`%s`.`%s`".formatted(databaseName, newTableName));
            newCreateTableSql = StringUtils.substringBefore(newCreateTableSql, "BUCKETS");
            newCreateTableSql = newCreateTableSql
                    + "BUCKETS " + estimateBucket
                    + """
                     \nPROPERTIES (
                        "replication_num" = "2",
                        "is_being_synced" = "false",
                        "compression" = "LZ4",
                        "enable_unique_key_merge_on_write" = "true",
                        "light_schema_change" = "true",
                        "enable_mow_light_delete" = "false",
                        "store_row_column" = "true"
                    );
                    """;
            
            String replaceTableData = "INSERT INTO `%s`.`%s` SELECT * FROM `%s`.`%s`;".formatted(databaseName, newTableName, databaseName, tableName);
            String dropTable = "DROP TABLE IF EXISTS `%s`.`%s`;".formatted(databaseName, tableName);
            String renameTable = "ALTER TABLE `%s`.`%s` RENAME %s;".formatted(databaseName, newTableName, tableName);
            
            if (false) {
                // todo 保存记录
                log.info(createTableSql);
                log.info(newCreateTableSql);
                log.info(replaceTableData);
                log.info(dropTable);
                log.info(renameTable);
            }
            
            // 新建表语句
            dorisMetadataRepository.execute(newCreateTableSql);
            // 数据替换
            dorisMetadataRepository.execute(replaceTableData);
            // 删除旧表
            dorisMetadataRepository.execute(dropTable);
            // 重命名新表
            dorisMetadataRepository.execute(renameTable);
        }
    }
}