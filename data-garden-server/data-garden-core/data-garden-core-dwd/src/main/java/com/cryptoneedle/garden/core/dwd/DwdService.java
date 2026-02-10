package com.cryptoneedle.garden.core.dwd;

import com.alibaba.excel.EasyExcel;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.core.doris.DorisService;
import com.cryptoneedle.garden.infrastructure.vo.dwd.LimsAssetCatalogImportVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-02-05
 */
@Slf4j
@Service
public class DwdService {
    
    public final DwdService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    public final DorisService dorisService;
    
    public DwdService(@Lazy DwdService service,
                      AddService addService,
                      SelectService selectService,
                      SaveService saveService,
                      DeleteService deleteService,
                      PatchService patchService,
                      DorisService dorisService) {
        this.service = service;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
        this.dorisService = dorisService;
    }
    
    public void parseLogicToCreateSql(MultipartFile file) throws IOException {
        String databaseName = select.config.dorisSchemaDwd();
        
        List<LimsAssetCatalogImportVo> excels = EasyExcel.read(file.getInputStream())
                                                         .head(LimsAssetCatalogImportVo.class)
                                                         .sheet(1)
                                                         .headRowNumber(3)
                                                         .doReadSync();
        fillMergedCells(excels);
        excels = excels.stream().filter(vo -> !Strings.CI.equals(vo.getFieldCode(), "GOVN_TIME")).toList();
        Map<String, List<LimsAssetCatalogImportVo>> excelMaps = excels.stream().collect(Collectors.groupingBy(LimsAssetCatalogImportVo::getLogicalEntityCode));
        excelMaps.forEach((key, value) -> {
            StringBuilder sql = new StringBuilder();
            String tableName = "%s_logic_%s_e".formatted(select.config.dorisTablePrefixDwd(), StringUtils.lowerCase(key));
            StringBuilder columnDefinition = new StringBuilder();
            StringBuilder insertColumns = new StringBuilder();
            StringBuilder insertSelectColumns = new StringBuilder();
            for (int i = 0; i < value.size(); i++) {
                LimsAssetCatalogImportVo vo = value.get(i);
                if (Strings.CI.equals(vo.getFieldCode(), "GOVN_TIME")) {
                    continue;
                }
                String field = StringUtils.lowerCase(vo.getFieldCode());
                String fieldComment = StringUtils.trimToEmpty(vo.getFieldName());
                if (i == value.size() - 1) {
                    columnDefinition.append("    `%s` VARCHAR(65533) COMMENT '%s',".formatted(field, fieldComment));
                } else {
                    columnDefinition.append("    `%s` VARCHAR(65533) COMMENT '%s',\n".formatted(field, fieldComment));
                }
                
                if (i == 0) {
                    insertColumns.append(" ").append(field).append("\n");
                } else if (i == value.size() - 1) {
                    insertColumns.append(", ").append(field);
                } else {
                    insertColumns.append(", ").append(field).append("\n");
                }
                
                if (i == 0) {
                    insertSelectColumns.append(" %s AS %s -- %s\n".formatted(vo.getOriginalFieldName(), field, fieldComment));
                } else if (i == value.size() - 1) {
                    insertSelectColumns.append("    , %s AS %s -- %s".formatted(vo.getOriginalFieldName(), field, fieldComment));
                } else {
                    insertSelectColumns.append("    , %s AS %s -- %s\n".formatted(vo.getOriginalFieldName(), field, fieldComment));
                }
            }
            sql.append("""
                    CREATE TABLE IF NOT EXISTS `%s`.`%s`(
                    %s
                        `govn_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间'
                    ) UNIQUE KEY()
                    COMMENT '%s'
                    DISTRIBUTED BY HASH() BUCKETS AUTO
                    PROPERTIES (
                        "replication_num" = "%s",
                        "is_being_synced" = "false",
                        "compression" = "LZ4",
                        "enable_unique_key_merge_on_write" = "true",
                        "light_schema_change" = "true",
                        "enable_mow_light_delete" = "false",
                        "store_row_column" = "true"
                    );\n
                    """.formatted(
                    databaseName,
                    tableName,
                    columnDefinition.toString(),
                    value.getFirst().getLogicalEntityName(),
                    select.config.dorisConfigReplicationNum())
            );
            sql.append("""
                    INSERT INTO %s.%s(%s
                                     , govn_time)
                    SELECT %s
                        , gather_time AS govn_time -- 治理时间
                    FROM %s.
                    WHERE gather_time >= DATE_SUB(CURRENT_DATE, ${beforeDay});\n
                    """.formatted(databaseName,
                    tableName,
                    insertColumns.toString(),
                    insertSelectColumns.toString(),
                    select.config.dorisSchemaOds()));
            // 导出
            try {
                String fileName = key + "-" + value.getFirst().getLogicalEntityName() + ".sql";
                Path path = Paths.get("/Users/cryptoneedle/CryptoNeedle/Obsidian/工作-绥通/4-数据治理/哈尔滨/数仓-LOGIC-代码生成", fileName);
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                Files.writeString(path, sql.toString());
                log.info("Exported SQL to {}", path);
            } catch (Exception e) {
                log.error("Export SQL error", e);
                throw new RuntimeException(e);
            }
        });
    }
    
    private void fillMergedCells(List<LimsAssetCatalogImportVo> list) {
        LimsAssetCatalogImportVo lastValid = null;
        for (LimsAssetCatalogImportVo vo : list) {
            if (StringUtils.isNotBlank(vo.getLogicalEntityCode())) {
                lastValid = vo;
            } else if (lastValid != null) {
                vo.setSeq(lastValid.getSeq());
                vo.setBusinessObjectName(lastValid.getBusinessObjectName());
                vo.setBusinessObjectCode(lastValid.getBusinessObjectCode());
                vo.setLogicalEntityName(lastValid.getLogicalEntityName());
                vo.setLogicalEntityCode(lastValid.getLogicalEntityCode());
                vo.setTableDescription(lastValid.getTableDescription());
                vo.setIssue(lastValid.getIssue());
            }
        }
    }
}