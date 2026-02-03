package com.cryptoneedle.garden.core.standard;

import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.core.doris.DorisService;
import com.cryptoneedle.garden.infrastructure.doris.DorisMetadataRepository;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisDatabase;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardColumn;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardTable;
import com.cryptoneedle.garden.infrastructure.vo.standard.StandardSaveVo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-02-03
 */
@Service
public class StandardService {
    
    public final StandardService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    public final DorisService dorisService;
    public final DorisMetadataRepository dorisMetadataRepository;
    
    public StandardService(@Lazy StandardService service,
                           AddService addService,
                           SelectService selectService,
                           SaveService saveService,
                           DeleteService deleteService,
                           PatchService patchService,
                           DorisService dorisService,
                           DorisMetadataRepository dorisMetadataRepository) {
        this.service = service;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
        this.dorisService = dorisService;
        this.dorisMetadataRepository = dorisMetadataRepository;
    }
    
    public void saveStandard(StandardSaveVo vo) {
        String databaseName = select.config.dorisSchemaStandard();
        DorisDatabase database = select.doris.database(databaseName);
        // 同步
        dorisService.syncDatabase(database, null);
        
        String tableName = "%s_%s".formatted(select.config.dorisTablePrefixStandard(), StringUtils.lowerCase(vo.getCode()));
        StandardTable table = select.standard.table(tableName);
        List<StandardSaveVo.Column> columns = vo.getColumns().stream()
                                                .peek(column -> column.setName(StringUtils.lowerCase(column.getName())))
                                                .sorted(Comparator.comparing(StandardSaveVo.Column::getSort))
                                                .toList();
        StandardSaveVo.Column firstColumn = columns.getFirst();
        if (table == null) {
            StringBuilder columnDefinition = new StringBuilder();
            for (StandardSaveVo.Column column : columns) {
                columnDefinition.append("    `%s` VARCHAR(65533) COMMENT '%s',\n".formatted(column.getName(), column.getComment()));
            }
            String sql = """
                    CREATE TABLE IF NOT EXISTS `%s`.`%s`(
                    %s
                        `gather_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间'
                    ) UNIQUE KEY(`%s`)
                    COMMENT '%s'
                    DISTRIBUTED BY HASH(`%s`) BUCKETS 1
                    PROPERTIES (
                        "replication_num" = "%s",
                        "is_being_synced" = "false",
                        "compression" = "LZ4",
                        "enable_unique_key_merge_on_write" = "true",
                        "light_schema_change" = "true",
                        "enable_mow_light_delete" = "false",
                        "store_row_column" = "true"
                    );""".formatted(
                    databaseName,
                    tableName,
                    columnDefinition.toString(),
                    firstColumn.getName(),
                    vo.getName(),
                    firstColumn.getName(),
                    select.config.dorisConfigReplicationNum());
            dorisMetadataRepository.execute(sql);
        } else {
            List<StandardColumn> standardColumns = select.standard.columns(tableName);
            if (!Strings.CI.equals(standardColumns.getFirst().getId().getColumnName(), firstColumn.getName())) {
                // 目的是手动确认是否需要变更
                throw new RuntimeException("数据库主键发生变化，需手动删除表后重新创建");
            }
            List<String> standardColumnNames = standardColumns.stream().map(column -> column.getId().getColumnName()).toList();
            Map<DorisColumnKey, StandardColumn> standardColumnMap = Maps.uniqueIndex(standardColumns, StandardColumn::getId);
            for (int i = 0; i < columns.size(); i++) {
                if (i == 0) {
                    continue;
                }
                StandardSaveVo.Column beforeColumn = columns.get(i - 1);
                StandardSaveVo.Column column = columns.get(i);
                String sql;
                StandardColumn standardColumn = standardColumnMap.get(new DorisColumnKey(databaseName, tableName, column.getName()));
                if (standardColumn != null) {
                    if (!standardColumn.getSort().equals(column.getSort())) {
                        // 修改
                        sql = "ALTER TABLE `%s`.`%s` MODIFY COLUMN `%s` VARCHAR(65533) COMMENT '%s' AFTER `%s`".formatted(databaseName, tableName, column.getName(), column.getComment(), beforeColumn.getName());
                        dorisMetadataRepository.execute(sql);
                    }
                } else {
                    // 新增
                    sql = "ALTER TABLE `%s`.`%s` ADD COLUMN `%s` VARCHAR(65533) COMMENT '%s' AFTER `%s`".formatted(databaseName, tableName, column.getName(), column.getComment(), beforeColumn.getName());
                    dorisMetadataRepository.execute(sql);
                }
            }
        }
        
        // 同步
        dorisService.syncDatabase(database, null);
    }
}