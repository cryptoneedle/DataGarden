package com.cryptoneedle.garden.core.source;

import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceColumn;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDatabase;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceTable;
import com.cryptoneedle.garden.spi.DataSourceProvider;
import com.cryptoneedle.garden.spi.DataSourceSpiLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-21
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SourceTransformService {
    
    public final SourceTransformService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    
    public SourceTransformService(@Lazy SourceTransformService sourceTransformService,
                                  AddService addService,
                                  SelectService selectService,
                                  SaveService saveService,
                                  DeleteService deleteService,
                                  PatchService patchService) {
        this.service = sourceTransformService;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
    }
    
    public void transTable(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        List<SourceTable> tables;
        if (database == null) {
            tables = select.source.tables(catalog.getId().getCatalogName());
        } else {
            if (table == null) {
                tables = select.source.tables(database.getId().getCatalogName(), database != null ? database.getId().getDatabaseName() : null);
            } else {
                tables = List.of(select.source.table(table.getId()));
            }
        }
        
        String ods = select.config.dorisTablePrefixOds();
        for (SourceTable sourceTable : tables) {
            if (!sourceTable.isTransTableLocked()) {
                sourceTable.setTransTableName(StringUtils.lowerCase("%s_%s_%s".formatted(ods, sourceTable.getSystemCode(), sourceTable.getId().getTableName())));
            }
            if (!sourceTable.isTransCommentLocked()) {
                sourceTable.setTransComment(sourceTable.getComment());
            }
            sourceTable.setTransBucketNum("AUTO");
        }
        save.source.tables(tables);
        
        for (SourceTable sourceTable : tables) {
            DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
            service.transColumns(sourceTable, provider);
        }
    }
    
    public void transColumns(SourceTable sourceTable, DataSourceProvider provider) {
        List<SourceColumn> columns = select.source.columns(sourceTable.getId().getCatalogName(), sourceTable.getId().getDatabaseName(), sourceTable.getId().getTableName());
        for (SourceColumn column : columns) {
            column.setDorisCatalog(sourceTable.getDorisCatalog());
            column.setSystemCode(sourceTable.getSystemCode());
            
            column.setTransTableName(sourceTable.getTransTableName());
            if (provider != null) {
                provider.transform(column);
            }
            
            column.setTransDataTypeFormat(column.transFullDataType());
            
            if (!column.getTransColumnLocked()) {
                String columnName = column.getId().getColumnName();
                // 去除开头_
                String transformColumnName = Strings.CI.removeStart(columnName, "_");
                // 去除结尾_
                transformColumnName = Strings.CI.removeEnd(transformColumnName, "_");
                // 去除连续_
                transformColumnName = transformColumnName.replaceAll("_{2,}", "_");
                // 小写
                transformColumnName = StringUtils.lowerCase(transformColumnName);
                column.setTransColumnName(transformColumnName);
            }
            
            if (!column.getTransCommentLocked()) {
                column.setTransComment(column.getComment());
            }
            if (column.getTransComment() == null) {
                column.setTransComment("");
            }
        }
        save.source.columns(columns);
    }
}