package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import com.cryptoneedle.garden.common.key.source.SourceColumnKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.repository.source.*;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceColumnAlterCommentVo;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceTableAlterCommentVo;
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
        List<SourceTable> tables = select.tables();
        for (SourceTable table : tables) {
            if (table.getDorisCatalog() == null
                    || table.getSystemCode() == null
                    || table.getCollectFrequency() == null
                    || table.getCollectTimePoint() == null) {
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
    
    /**
     * SourceColumn
     */
    
    /**
     * SourceDimension
     */
}