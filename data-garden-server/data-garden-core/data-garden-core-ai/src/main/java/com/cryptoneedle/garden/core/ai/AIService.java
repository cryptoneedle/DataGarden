package com.cryptoneedle.garden.core.ai;

import com.cryptoneedle.garden.common.constants.CommonConstant;
import com.cryptoneedle.garden.common.key.doris.*;
import com.cryptoneedle.garden.core.crud.SelectService;
import com.cryptoneedle.garden.core.mapping.MappingService;
import com.cryptoneedle.garden.core.ods.OdsService;
import com.cryptoneedle.garden.core.ai.neo4j.service.Neo4jLineageService;
import com.cryptoneedle.garden.infrastructure.doris.DorisMetadataRepository;
import com.cryptoneedle.garden.infrastructure.dto.DwdColumnGen;
import com.cryptoneedle.garden.infrastructure.entity.dolphinScheduler.DolphinSchedulerTask;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisLineageColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisLineageTable;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisShowCreateTable;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumnRely;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTableRely;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumnTranslate;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisLineageColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisLineageTableRepository;
import com.cryptoneedle.garden.infrastructure.vo.dwd.DwdGenResultVo;
import com.cryptoneedle.garden.infrastructure.vo.lineage.SqlLineageAnalysisResultVo;
import com.cryptoneedle.garden.infrastructure.vo.ods.ColumnTranslateResultVo;
import com.google.common.collect.Maps;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-30
 */
@Service
public class AIService {
    
    @Autowired
    private SelectService selectService;
    @Autowired
    private OdsService odsService;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private DorisMetadataRepository dorisMetadataRepository;
    @Autowired
    private ChatClient chatClient;
    @Autowired
    @Qualifier("dorisJdbcTemplate")
    private JdbcTemplate dorisJdbcTemplate;
    @Autowired
    @Qualifier("dolphinSchedulerJdbcTemplate")
    private JdbcTemplate dolphinSchedulerJdbcTemplate;
    @Autowired
    private DorisLineageTableRepository dorisLineageTableRepository;
    @Autowired
    private DorisLineageColumnRepository dorisLineageColumnRepository;
    @Autowired
    private Neo4jLineageService neo4jLineageService;
    
    public void translateOdsColumn(String tableName, String columnName) {
        String ods = selectService.config.dorisSchemaOds();
        OdsColumn odsColumn = selectService.ods.column(new DorisColumnKey(ods, tableName, columnName));
        List<String> enums = dorisJdbcTemplate.queryForList("SELECT DISTINCT %s FROM %s.%s".formatted(columnName, ods, tableName), String.class);
        if (enums.size() > 255) {
            throw new RuntimeException("枚举值数量超过255");
        }
        String enumsString = enums.stream().collect(Collectors.joining("、"));
        String comment = odsColumn.getComment();
        var outputConverter = new BeanOutputConverter<>(ColumnTranslateResultVo.class);
        ColumnTranslateResultVo vo = chatClient.prompt()
                                               .system("你是一个数据治理专家。")
                                               .user(u -> u.text("""
                                                                         以下内容是数据库表的字段名称、字段说明信息，需要从重提取出字段的枚举值、枚举值对应的翻译值。
                                                                         
                                                                         字段：{columnName}
                                                                         说明：{comment}
                                                                         字段值枚举：{enumsString}
                                                                         
                                                                         ----------------
                                                                         {format}
                                                                         """)
                                                           .param("columnName", columnName)
                                                           .param("enumsString", enumsString)
                                                           .param("comment", comment)
                                                           .param("format", outputConverter.getFormat()))
                                               .call()
                                               .entity(outputConverter);
        List<ColumnTranslateResultVo.ColumnTranslateResult> results = null;
        if (vo != null && !vo.getResults().isEmpty()) {
            List<OdsColumnTranslate> odsColumnTranslateList = vo.getResults().stream().map(translate -> {
                OdsColumnTranslateKey key = OdsColumnTranslateKey.builder()
                                                                 .databaseName(ods)
                                                                 .tableName(tableName)
                                                                 .columnName(columnName)
                                                                 .value(translate.getValue())
                                                                 .build();
                return OdsColumnTranslate.builder()
                                         .id(key)
                                         .translate(translate.getTranslateValue())
                                         .build();
            }).toList();
            odsService.saveTranslateColumnList(tableName, columnName, odsColumnTranslateList);
        }
    }
    
    public DwdGenResultVo generateDwdTable(String odsTableName) {
        String odsDatabaseName = selectService.config.dorisSchemaOds();
        String dwdDatabaseName = selectService.config.dorisSchemaDwd();
        
        // ODS 表
        OdsTable odsTable = selectService.ods.table(new DorisTableKey(odsDatabaseName, odsTableName));
        
        // ODS 表建表语句
        DorisShowCreateTable odsDorisShowCreateTable = dorisMetadataRepository.execShowCreateTable(CommonConstant.DORIS_CATALOG, odsDatabaseName, odsTableName);
        
        // ODS 表建表
        List<OdsColumn> odsColumns = selectService.ods.columns(odsTableName);
        
        // 获取映射关系
        List<MappingColumn> allMappingColumns = selectService.mapping.columnsByOdsRelyMapping(odsTableName);
        List<String> allMappingColumnNames = allMappingColumns.stream()
                                                              .map(column -> column.getId().getColumnName())
                                                              .distinct()
                                                              .toList();
        Map<String, MappingColumn> allMappingColumnMaps = Maps.uniqueIndex(allMappingColumns, column -> column.getId()
                                                                                                              .getTableName() + column.getId()
                                                                                                                                      .getColumnName());
        List<MappingTableRely> mappingTableRelies = selectService.mapping.mappingTableRelyListBySource(odsDatabaseName, odsTableName);
        List<MappingColumnRely> mappingColumnRelies = selectService.mapping.listColumnRelysBySource(odsTableName);
        
        // DWD表名
        String dwdTableName = odsTableName.replaceFirst(odsDatabaseName + "_", dwdDatabaseName + "_");
        
        // 构建 DWD 字段
        // 唯一列
        List<DwdColumnGen> uniqueKeyList = new ArrayList<>();
        // 正常列
        List<DwdColumnGen> normalColumnList = new ArrayList<>();
        // 映射列
        List<DwdColumnGen> mappingColumnList = new ArrayList<>();
        // todo 公共列
        //List<DwdColumnGen> commonColumnList = new ArrayList<>();
        
        // 构建唯一列
        for (OdsColumn odsColumn : odsColumns.stream()
                                             .filter(column -> column.getColumnType().equals("UNI"))
                                             .filter(column -> !column.getId()
                                                                      .getColumnName()
                                                                      .equalsIgnoreCase("gather_time"))
                                             .toList()) {
            String columnName = odsColumn.getId().getColumnName();
            if (allMappingColumnNames.contains(columnName)) {
                columnName = "origin_" + columnName;
            }
            uniqueKeyList.add(DwdColumnGen.builder()
                                          .originTableName(odsColumn.getId().getTableName())
                                          .originColumnName(odsColumn.getId().getColumnName())
                                          .columnName(columnName)
                                          .columnComment(odsColumn.getComment())
                                          .columnDataTypeFormat(odsColumn.getDataTypeFormat())
                                          // TODO 暂限制主键不能被翻译
                                          .translated(false)
                                          .build());
        }
        
        // 构建正常列
        for (OdsColumn odsColumn : odsColumns.stream()
                                             .filter(column -> !column.getColumnType().equals("UNI"))
                                             .filter(column -> !column.getId()
                                                                      .getColumnName()
                                                                      .equalsIgnoreCase("gather_time"))
                                             .toList()) {
            String columnName = odsColumn.getId().getColumnName();
            if (allMappingColumnNames.contains(columnName)) {
                columnName = "origin_" + columnName;
            }
            normalColumnList.add(DwdColumnGen.builder()
                                             .originTableName(odsColumn.getId().getTableName())
                                             .originColumnName(odsColumn.getId().getColumnName())
                                             .columnName(columnName)
                                             .columnComment(odsColumn.getComment())
                                             .columnDataTypeFormat(odsColumn.getDataTypeFormat())
                                             .translated(false)
                                             .build());
            // 翻译字段
            if (odsColumn.getTranslatable() != null && odsColumn.getTranslatable()) {
                normalColumnList.add(DwdColumnGen.builder()
                                                 .originTableName(odsColumn.getId().getTableName())
                                                 .originColumnName(odsColumn.getId().getColumnName())
                                                 .columnName(columnName + "_dscr")
                                                 .columnComment("[译]-" + odsColumn.getComment())
                                                 .columnDataTypeFormat(odsColumn.getDataTypeFormat())
                                                 .translated(true)
                                                 .build());
            }
        }
        
        // 构建映射列
        for (MappingTableRely mappingTableRely : mappingTableRelies) {
            List<MappingColumn> mappingColumns = selectService.mapping.columns(mappingTableRely.getId()
                                                                                               .getMappingTableName());
            for (MappingColumn mappingColumn : mappingColumns.stream()
                                                             .filter(column -> !column.getId()
                                                                                      .getColumnName()
                                                                                      .contains("reference_"))
                                                             .filter(column -> !column.getId()
                                                                                      .getColumnName()
                                                                                      .equalsIgnoreCase("gather_time"))
                                                             .toList()) {
                if (!mappingColumnList.stream()
                                      .map(DwdColumnGen::getColumnName)
                                      .toList()
                                      .contains(mappingColumn.getId().getColumnName())) {
                    mappingColumnList.add(DwdColumnGen.builder()
                                                      .originTableName(mappingColumn.getId().getTableName())
                                                      .originColumnName(mappingColumn.getId().getColumnName())
                                                      .columnName(mappingColumn.getId().getColumnName())
                                                      .columnComment(mappingColumn.getComment())
                                                      .columnDataTypeFormat(mappingColumn.getDataTypeFormat())
                                                      .translated(true)
                                                      .build());
                }
            }
        }
        
        String createTableSql = buildCreateTable(dwdDatabaseName, dwdTableName, odsTable, uniqueKeyList, normalColumnList, mappingColumnList);
        String insertTableSql = buildInsertTable(dwdDatabaseName, dwdTableName, odsDatabaseName, odsTableName, uniqueKeyList, normalColumnList, mappingColumnList, mappingTableRelies, mappingColumnRelies);
        
        DwdGenResultVo resultVo = new DwdGenResultVo();
        resultVo.setCreateTableSql(createTableSql);
        resultVo.setInsertTableSql(insertTableSql);
        return resultVo;
    }
    
    private String buildInsertTable(String dwdDatabaseName,
                                    String dwdTableName,
                                    String odsDatabaseName,
                                    String odsTableName,
                                    List<DwdColumnGen> uniqueKeyList,
                                    List<DwdColumnGen> normalColumnList,
                                    List<DwdColumnGen> mappingColumnList,
                                    List<MappingTableRely> mappingTableRelies,
                                    List<MappingColumnRely> mappingColumnRelies) {
        // 合并所有列
        List<DwdColumnGen> allColumns = new ArrayList<>();
        allColumns.addAll(uniqueKeyList);
        allColumns.addAll(normalColumnList);
        allColumns.addAll(mappingColumnList);
        
        // INSERT 列名列表
        String insertColumns = allColumns.stream()
                                         .map(col -> "    `" + col.getColumnName() + "`")
                                         .collect(Collectors.joining(",\n"));
        
        // SELECT 字段列表 — 格式: 原始表名.`字段名` AS `目标列名`
        String selectFields = allColumns.stream()
                                        .map(col -> "    " + col.getOriginTableName() + ".`" + col.getOriginColumnName() + "` AS `" + col.getColumnName() + "`")
                                        .collect(Collectors.joining(",\n"));
        
        // FROM 子句 — 不取别名
        String fromClause = "`" + odsDatabaseName + "`.`" + odsTableName + "`";
        
        // 按映射表名分组 MappingColumnRely
        Map<String, List<MappingColumnRely>> reliesByTable = mappingColumnRelies.stream()
                                                                                .collect(Collectors.groupingBy(r -> r.getId()
                                                                                                                     .getMappingTableName()));
        
        // LEFT JOIN 子句 — 不取别名，直接使用表名引用字段
        String joinClauses = mappingTableRelies.stream()
                                               .map(rely -> {
                                                   String mappingTableName = rely.getId().getMappingTableName();
                                                   String mappingDb = rely.getId().getMappingDatabaseName();
                                                   
                                                   List<MappingColumnRely> relies = reliesByTable.get(mappingTableName);
                                                   if (relies == null || relies.isEmpty()) {
                                                       return "";
                                                   }
                                                   
                                                   String onConditions = relies.stream()
                                                                               .map(r -> odsTableName + ".`" + r.getId()
                                                                                                                .getSourceColumnName() + "` = " + mappingTableName + ".`" + r.getId()
                                                                                                                                                                             .getMappingColumnName() + "`")
                                                                               .collect(Collectors.joining(" AND "));
                                                   
                                                   return "\nLEFT JOIN `" + mappingDb + "`.`" + mappingTableName + "` ON " + onConditions;
                                               })
                                               .filter(s -> !s.isEmpty())
                                               .collect(Collectors.joining());
        
        return """
                INSERT INTO `%s`.`%s`(
                %s
                )
                SELECT
                %s
                FROM %s
                %s
                """
                .formatted(dwdDatabaseName, dwdTableName, insertColumns, selectFields, fromClause, joinClauses);
    }
    
    private String buildCreateTable(String dwdDatabaseName,
                                    String dwdTableName,
                                    OdsTable odsTable,
                                    List<DwdColumnGen> uniqueKeyList,
                                    List<DwdColumnGen> normalColumnList,
                                    List<DwdColumnGen> mappingColumnList) {
        // 拼接所有列定义（唯一列 + 正常列 + 映射列）
        List<DwdColumnGen> allColumns = new ArrayList<>();
        allColumns.addAll(uniqueKeyList);
        allColumns.addAll(normalColumnList);
        allColumns.addAll(mappingColumnList);
        
        String columnDefinition = allColumns.stream()
                                            .map(col -> "    `" + col.getColumnName() + "` " + col.getColumnDataTypeFormat() + " COMMENT '" + col.getColumnComment() + "'")
                                            .collect(Collectors.joining(",\n"));
        
        // 唯一键列（带反引号，逗号拼接）
        String uniqueKeys = uniqueKeyList.stream()
                                         .map(col -> "`" + col.getColumnName() + "`")
                                         .collect(Collectors.joining(", "));
        
        String replicationNum = selectService.config.dorisConfigReplicationNum();
        String comment = odsTable.getComment();
        
        return """
                CREATE TABLE IF NOT EXISTS `%s`.`%s`(
                    %s
                    `gather_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间'
                ) UNIQUE KEY(%s)
                COMMENT '%s'
                DISTRIBUTED BY HASH(%s) BUCKETS AUTO
                PROPERTIES (
                    "replication_num" = "%s",
                    "is_being_synced" = "false",
                    "compression" = "LZ4",
                    "enable_unique_key_merge_on_write" = "true",
                    "light_schema_change" = "true",
                    "enable_mow_light_delete" = "false",
                    "store_row_column" = "true"
                );
                """
                .formatted(dwdDatabaseName, dwdTableName, columnDefinition, uniqueKeys, comment, uniqueKeys, replicationNum);
    }
    
    public void dolphinSchedulerDataLineage() {
        // 步骤 1: 从 DolphinScheduler 数据库查询所有 SQL 类型的任务定义
        List<DolphinSchedulerTask> sqlTasks = dolphinSchedulerJdbcTemplate.query("""
                                                                                         SELECT td.code        AS code,
                                                                                                td.task_type   AS taskType,
                                                                                                td.task_params AS json
                                                                                         FROM t_ds_process_definition pd
                                                                                                  JOIN t_ds_process_task_relation ptr
                                                                                                       ON pd.code = ptr.process_definition_code AND pd.version = ptr.process_definition_version
                                                                                                  JOIN t_ds_task_definition td ON ptr.post_task_code = td.code AND ptr.post_task_version = td.version""",
                                                                                 (rs, rowNum) -> DolphinSchedulerTask.builder()
                                                                                                                     .code(rs.getString("code"))
                                                                                                                     .taskType(rs.getString("taskType"))
                                                                                                                     .json(rs.getString("json"))
                                                                                                                     .build());
        sqlTasks.stream().filter(task -> task.getTaskType().equals("SQL")).forEach(
                task -> {
                    String sql = task.getJson();
                    String taskCode = task.getCode();
                    
                    // 使用 AI 分析 SQL 语句，识别表与表之间的血缘关系
                    var outputConverter = new BeanOutputConverter<>(SqlLineageAnalysisResultVo.class);
                    SqlLineageAnalysisResultVo lineageResult = chatClient.prompt()
                                                                         .system("你是一个数据血缘分析专家，擅长从 SQL 语句中提取表和字段的血缘关系。")
                                                                         .user(u -> u.text("""
                                                                                                   请分析以下 SQL 语句，提取其中的血缘关系信息：
                                                                                                   
                                                                                                   SQL 语句：
                                                                                                   {sql}
                                                                                                   
                                                                                                   请返回以下信息：
                                                                                                   1. 目标表列表（INSERT INTO/CREATE TABLE 中的表）
                                                                                                   2. 源表列表（FROM/JOIN 中的表）
                                                                                                   3. 表级血缘关系（上游表 -> 下游表）
                                                                                                   4. 字段级血缘关系（源字段 -> 目标字段）
                                                                                                   
                                                                                                   注意：
                                                                                                   - 尽量识别完整的数据库名.表名格式，并排除中间表或临时表
                                                                                                   - 字段血缘关系需要包含转换逻辑（如函数调用、计算表达式等）
                                                                                                   
                                                                                                   ----------------
                                                                                                   {format}
                                                                                                   """)
                                                                                     .param("sql", sql)
                                                                                     .param("format", outputConverter.getFormat()))
                                                                         .call()
                                                                         .entity(outputConverter);
                    
                    if (lineageResult != null) {
                        // 获取表级血缘关系
                        List<SqlLineageAnalysisResultVo.TableLineage> tableLineages = lineageResult.getTableLineages();
                        
                        // 获取字段级血缘关系
                        List<SqlLineageAnalysisResultVo.FieldLineage> fieldLineages = lineageResult.getFieldLineages();
                        
                        // 转换为 DorisLineageTable 实体并保存到关系数据库
                        if (tableLineages != null && !tableLineages.isEmpty()) {
                            List<DorisLineageTable> dorisLineageTables = tableLineages.stream()
                                                                                      .filter(tl -> tl.getToDatabaseName() != null && tl.getToTableName() != null
                                                                                              && tl.getFromDatabaseName() != null && tl.getFromTableName() != null)
                                                                                      .map(tl -> {
                                                                                          DorisLineageTableKey key = DorisLineageTableKey.builder()
                                                                                                                                         .toDatabaseName(tl.getToDatabaseName())
                                                                                                                                         .toTableName(tl.getToTableName())
                                                                                                                                         .fromDatabaseName(tl.getFromDatabaseName())
                                                                                                                                         .fromTableName(tl.getFromTableName())
                                                                                                                                         .build();
                                                                                          return DorisLineageTable.builder()
                                                                                                                  .id(key)
                                                                                                                  .build();
                                                                                      })
                                                                                      .toList();
                            
                            if (!dorisLineageTables.isEmpty()) {
                                dorisLineageTableRepository.saveAll(dorisLineageTables);
                            }
                            
                            // 同时保存到 Neo4j 图数据库
                            List<Neo4jLineageService.TableLineageInfo> neo4jTableLineages = tableLineages.stream()
                                                                                                         .filter(tl -> tl.getToDatabaseName() != null && tl.getToTableName() != null
                                                                                                                 && tl.getFromDatabaseName() != null && tl.getFromTableName() != null)
                                                                                                         .map(tl -> new Neo4jLineageService.TableLineageInfo(
                                                                                                                 tl.getFromDatabaseName(), tl.getFromTableName(),
                                                                                                                 tl.getToDatabaseName(), tl.getToTableName(),
                                                                                                                 "SQL任务: " + taskCode))
                                                                                                         .toList();
                            
                            if (!neo4jTableLineages.isEmpty()) {
                                neo4jLineageService.batchCreateTableLineage(neo4jTableLineages, taskCode);
                            }
                        }
                        
                        // 转换为 DorisLineageColumn 实体并保存到关系数据库
                        if (fieldLineages != null && !fieldLineages.isEmpty()) {
                            List<DorisLineageColumn> dorisLineageColumns = fieldLineages.stream()
                                                                                        .filter(fl -> fl.getToDatabaseName() != null && fl.getToTableName() != null && fl.getToColumnName() != null
                                                                                                && fl.getFromDatabaseName() != null && fl.getFromTableName() != null && fl.getFromColumnName() != null)
                                                                                        .map(fl -> {
                                                                                            DorisLineageColumnKey key = DorisLineageColumnKey.builder()
                                                                                                                                             .toDatabaseName(fl.getToDatabaseName())
                                                                                                                                             .toTableName(fl.getToTableName())
                                                                                                                                             .toColumnName(fl.getToColumnName())
                                                                                                                                             .fromDatabaseName(fl.getFromDatabaseName())
                                                                                                                                             .fromTableName(fl.getFromTableName())
                                                                                                                                             .fromColumnName(fl.getFromColumnName())
                                                                                                                                             .build();
                                                                                            return DorisLineageColumn.builder()
                                                                                                                     .id(key)
                                                                                                                     .transformLogic(fl.getTransformLogic())
                                                                                                                     .build();
                                                                                        })
                                                                                        .toList();
                            
                            if (!dorisLineageColumns.isEmpty()) {
                                dorisLineageColumnRepository.saveAll(dorisLineageColumns);
                            }
                            
                            // 同时保存到 Neo4j 图数据库
                            List<Neo4jLineageService.ColumnLineageInfo> neo4jColumnLineages = fieldLineages.stream()
                                                                                                           .filter(fl -> fl.getToDatabaseName() != null && fl.getToTableName() != null && fl.getToColumnName() != null
                                                                                                                   && fl.getFromDatabaseName() != null && fl.getFromTableName() != null && fl.getFromColumnName() != null)
                                                                                                           .map(fl -> new Neo4jLineageService.ColumnLineageInfo(
                                                                                                                   fl.getFromDatabaseName(), fl.getFromTableName(), fl.getFromColumnName(),
                                                                                                                   fl.getToDatabaseName(), fl.getToTableName(), fl.getToColumnName(),
                                                                                                                   fl.getTransformLogic(), "SQL任务: " + taskCode))
                                                                                                           .toList();
                            
                            if (!neo4jColumnLineages.isEmpty()) {
                                neo4jLineageService.batchCreateColumnLineage(neo4jColumnLineages, taskCode);
                            }
                        }
                    }
                }
        );
    }
}