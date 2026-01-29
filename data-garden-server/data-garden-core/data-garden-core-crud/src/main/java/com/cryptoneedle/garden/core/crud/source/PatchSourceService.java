package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.common.enums.DorisDataType;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import com.cryptoneedle.garden.common.enums.SourceTimeType;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.repository.source.*;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceColumnAlterCommentVo;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceTableAlterCommentVo;
import com.cryptoneedle.garden.spi.DataSourceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>description: 部分更新数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchSourceService {
    
    private final SelectConfigService selectConfigService;
    private final SelectSourceService select;
    private final SaveSourceService save;
    private final SourceCatalogRepository sourceCatalogRepository;
    private final SourceDatabaseRepository sourceDatabaseRepository;
    private final SourceTableRepository sourceTableRepository;
    private final SourceColumnRepository sourceColumnRepository;
    private final SourceDimensionRepository sourceDimensionRepository;
    
    public PatchSourceService(SelectConfigService selectConfigService,
                              SelectSourceService selectSourceService,
                              SaveSourceService saveSourceService,
                              SourceCatalogRepository sourceCatalogRepository,
                              SourceDatabaseRepository sourceDatabaseRepository,
                              SourceTableRepository sourceTableRepository,
                              SourceColumnRepository sourceColumnRepository,
                              SourceDimensionRepository sourceDimensionRepository) {
        this.selectConfigService = selectConfigService;
        this.select = selectSourceService;
        this.save = saveSourceService;
        this.sourceCatalogRepository = sourceCatalogRepository;
        this.sourceDatabaseRepository = sourceDatabaseRepository;
        this.sourceTableRepository = sourceTableRepository;
        this.sourceColumnRepository = sourceColumnRepository;
        this.sourceDimensionRepository = sourceDimensionRepository;
    }
    
    /**
     * SourceCatalog
     */
    
    public void catalogEnabled(SourceCatalog catalog, Boolean enabled) {
        catalog.setEnabled(enabled);
        save.catalog(catalog);
    }
    
    /**
     * SourceDatabase
     */
    public void databaseDefault(SourceCatalog catalog) {
        List<SourceDatabase> databases = select.databases();
        for (SourceDatabase database : databases) {
            if (database.getDorisCatalog() == null
                    || database.getSystemCode() == null
                    || database.getCollectFrequency() == null
                    || database.getCollectTimePoint() == null) {
                if (database.getDorisCatalog() == null) {
                    database.setDorisCatalog(catalog.getDorisCatalog());
                }
                if (database.getSystemCode() == null) {
                    database.setSystemCode(catalog.getSystemCode());
                }
                if (database.getCollectFrequency() == null) {
                    database.setCollectFrequency(catalog.getCollectFrequency());
                }
                if (database.getCollectTimePoint() == null) {
                    database.setCollectTimePoint(catalog.getCollectTimePoint());
                }
                save.database(database);
            }
            
        }
    }
    
    public void databaseEnabled(SourceDatabase database, Boolean enabled) {
        database.setEnabled(enabled);
        save.database(database);
    }
    
    /**
     * SourceTable
     */
    public void tableDefault(SourceCatalog catalog) {
        List<SourceTable> tables = select.tables(catalog.getId().getCatalogName());
        for (SourceTable table : tables) {
            if (table.getDorisCatalog() == null
                    || table.getSystemCode() == null
                    || table.getCollectFrequency() == null
                    || table.getCollectTimePoint() == null
                    || table.getCollectGroupNum() == null
                    || table.getRowNum() == null) {
                if (table.getDorisCatalog() == null) {
                    table.setDorisCatalog(catalog.getDorisCatalog());
                }
                if (table.getSystemCode() == null) {
                    table.setSystemCode(catalog.getSystemCode());
                }
                if (table.getCollectFrequency() == null) {
                    table.setCollectFrequency(catalog.getCollectFrequency());
                }
                if (table.getCollectTimePoint() == null) {
                    table.setCollectTimePoint(catalog.getCollectTimePoint());
                }
                if (table.getCollectGroupNum() == null) {
                    table.setCollectGroupNum(0);
                }
                if (table.getRowNum() == null) {
                    Long rowNum = DataSourceExecutor.selectRowNum(catalog, table);
                    table.setRowNum(rowNum);
                }
                save.table(table);
            }
            
        }
    }
    
    public void tableEnabled(SourceTable table, Boolean enabled) {
        table.setEnabled(enabled);
        save.table(table);
    }
    
    public void dimensions(String catalogName, String databaseName, String tableName, String dimensionName, SourceDimensionType dimensionType, Boolean enabled) {
        List<SourceDimension> dimensions = select.dimensions(catalogName, databaseName, tableName);
        for (SourceDimension dimension : dimensions) {
            if (Strings.CI.equals(dimension.getId().getDimensionName(), dimensionName) && dimension.getId().getDimensionType().equals(dimensionType)) {
                dimension.setEnabled(enabled);
            } else {
                if (enabled) {
                    dimension.setEnabled(!enabled);
                }
            }
        }
        save.dimensions(dimensions);
        
        if (enabled && !dimensions.isEmpty()) {
            SourceTable sourceTable = select.table(new SourceTableKey(catalogName, databaseName, tableName));
            sourceTable.setDimension(dimensionName);
            save.table(sourceTable);
        }
        
        
    }
    
    public void tableCommentBatch(String catalogName, String databaseName, List<SourceTableAlterCommentVo> vos) {
        List<SourceTable> tables = vos.stream()
                                      .map(vo -> {
                                          SourceTable table = select.table(new SourceTableKey(catalogName, databaseName, vo.getTableName()));
                                          if (table != null) {
                                              table.setTransComment(vo.getComment());
                                              table.setTransCommentLocked(true);
                                          }
                                          return table;
                                      })
                                      .filter(Objects::nonNull)
                                      .toList();
        save.tables(tables);
    }
    
    public void columnCommentBatch(String catalogName, String databaseName, List<SourceColumnAlterCommentVo> vos) {
        List<SourceColumn> columns = vos.stream()
                                        .map(vo -> {
                                            SourceColumn column = select.column(new SourceColumnKey(catalogName, databaseName, vo.getTableName(), vo.getColumnName()));
                                            if (column != null) {
                                                column.setTransComment(vo.getComment());
                                                column.setTransCommentLocked(true);
                                            }
                                            return column;
                                        })
                                        .filter(Objects::nonNull)
                                        .toList();
        save.columns(columns);
    }
    
    public void tableEnabledBatch(String catalogName, String databaseName, List<String> tableNames) {
        List<String> doList = select.tables(catalogName, databaseName).stream()
                                    .filter(table -> tableNames.contains(table.getId().getTableName()))
                                    .peek(table -> {
                                        table.setEnabled(true);
                                        save.table(table);
                                    })
                                    .map(table -> table.getId().getTableName())
                                    .toList();
        log.info("未开启表：{}", tableNames.stream()
                                          .filter(table -> !doList.contains(table))
                                          .collect(Collectors.joining()));
    }
    
    public void columnIncremented(SourceCatalog catalog, SourceDatabase database, SourceTable table, SourceColumn column, Boolean incremented, String timeType) {
        if (incremented) {
            SourceTimeType sourceTimeType = SourceTimeType.valueOf(timeType);
            DorisDataType transDataType = column.getTransDataType();
            if (column.getNullNum() != null && column.getNullNum() > 0) {
                throw new RuntimeException("增量字段不允许存在NULL");
            }
            if (!DorisDataType.CHAR.equals(transDataType)
                    && !DorisDataType.VARCHAR.equals(transDataType)
                    && !DorisDataType.DATE.equals(transDataType)
                    && !DorisDataType.DATETIME.equals(transDataType)) {
                throw new RuntimeException("不支持的数据类型");
            } else {
                //                if (DorisDataType.CHAR.equals(transDataType) || DorisDataType.VARCHAR.equals(transDataType)) {
                //                    if (!DataSourceExecutor.validStrToDateSql(catalog, column)) {
                //                        throw new RuntimeException("不支持的转换");
                //                    }
                //                } else if (!DorisDataType.DATE.equals(transDataType) && !DorisDataType.DATETIME.equals(transDataType)) {
                //                    // TODO
                //                    throw new RuntimeException("不支持的数据类型");
                //                }
            }
            column.setTimeType(sourceTimeType);
        } else {
            column.setTimeType(null);
        }
        column.setIncremented(incremented);
        save.column(column);
    }
    
    public void catalogCollectFrequency(SourceCatalog catalog, SourceCollectFrequencyType sourceCollectFrequencyType) {
        catalog.setCollectFrequency(sourceCollectFrequencyType);
        save.catalog(catalog);
    }
    
    public void databaseCollectFrequency(SourceDatabase database, SourceCollectFrequencyType sourceCollectFrequencyType) {
        database.setCollectFrequency(sourceCollectFrequencyType);
        save.database(database);
    }
    
    public void tableCollectFrequency(SourceTable table, SourceCollectFrequencyType sourceCollectFrequencyType) {
        table.setCollectFrequency(sourceCollectFrequencyType);
        save.table(table);
    }
    
    public void catalogCollectTimePoint(SourceCatalog catalog, Integer collectTimePoint) {
        catalog.setCollectTimePoint(collectTimePoint);
        save.catalog(catalog);
    }
    
    public void databaseCollectTimePoint(SourceDatabase database, Integer collectTimePoint) {
        database.setCollectTimePoint(collectTimePoint);
        save.database(database);
    }
    
    public void tableCollectTimePoint(SourceTable table, Integer collectTimePoint) {
        table.setCollectTimePoint(collectTimePoint);
        save.table(table);
    }
    
    
    /**
     * SourceColumn
     */
    
    /**
     * SourceDimension
     */
}