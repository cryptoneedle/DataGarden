package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.core.crud.source.*;
import com.cryptoneedle.garden.core.source.SourceService;
import com.cryptoneedle.garden.core.source.SourceSyncService;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDatabase;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceTable;
import com.cryptoneedle.garden.infrastructure.vo.dimension.SourceDimensionSaveVo;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceCatalogSaveVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-20
 */
@RestController
@RequestMapping("/source")
public class SourceController {
    
    private final SourceService sourceService;
    private final SourceSyncService sourceSyncService;
    public final AddSourceService add;
    public final SelectSourceService select;
    public final SaveSourceService save;
    public final DeleteSourceService delete;
    public final PatchSourceService patch;
    
    public SourceController(SourceService sourceService,
                            SourceSyncService sourceSyncService,
                            AddSourceService add,
                            SelectSourceService select,
                            SaveSourceService save,
                            DeleteSourceService delete,
                            PatchSourceService patch) {
        this.sourceService = sourceService;
        this.sourceSyncService = sourceSyncService;
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
    }
    
    @PostMapping("/catalog/test/server")
    public Result<?> testAddCatalogServer(@Valid @RequestBody SourceCatalogSaveVo vo) {
        return Result.success(sourceService.testServer(vo.sourceCatalog(), vo.isNeedStore()));
    }
    
    @PostMapping("/catalog/test/jdbc")
    public Result<?> testAddCatalogJdbc(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceService.fillPassword(vo);
        return Result.success(sourceService.testJdbc(vo.sourceCatalog(), vo.isNeedStore()));
    }
    
    @PostMapping("/catalog/test/doris")
    public Result<?> testAddCatalogDoris(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceService.fillPassword(vo);
        return Result.success(sourceService.testDoris(vo.sourceCatalog(), vo.isNeedStore()));
    }
    
    @PostMapping("/catalog/add")
    public Result<?> addCatalog(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceService.addVo(vo);
        return Result.success();
    }
    
    @PostMapping("/catalog/save")
    public Result<?> saveCatalog(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceService.fillPassword(vo);
        sourceService.saveVo(vo);
        return Result.success();
    }
    
    @PostMapping("/catalog/tree")
    public Result<?> catalogTree() {
        return Result.success(select.tree());
    }
    
    @PostMapping("/catalog/{catalogName}/sync")
    public Result<?> syncCatalog(@PathVariable("catalogName") String catalogName) throws EntityNotFoundException {
        SourceCatalog catalog = select.catalogCheck(new SourceCatalogKey(catalogName));
        sourceSyncService.syncCatalog(catalog);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/sync")
    public Result<?> syncDatabase(@PathVariable("catalogName") String catalogName,
                                  @PathVariable("databaseName") String databaseName) throws EntityNotFoundException {
        SourceCatalog catalog = select.catalogCheck(new SourceCatalogKey(catalogName));
        SourceDatabase database = select.databaseCheck(new SourceDatabaseKey(catalogName, databaseName));
        sourceSyncService.syncDatabase(catalog, database);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/{tableName}/sync")
    public Result<?> syncTable(@PathVariable("catalogName") String catalogName,
                               @PathVariable("databaseName") String databaseName,
                               @PathVariable("tableName") String tableName) throws EntityNotFoundException {
        SourceCatalog catalog = select.catalogCheck(new SourceCatalogKey(catalogName));
        SourceDatabase database = select.databaseCheck(new SourceDatabaseKey(catalogName, databaseName));
        SourceTable table = select.tableCheck(new SourceTableKey(catalogName, databaseName, tableName));
        sourceSyncService.syncTable(catalog, database, table);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/list")
    public Result<?> databases(@PathVariable("catalogName") String catalogName,
                               @RequestParam Boolean onlyEnabled) {
        if (onlyEnabled) {
            return Result.success(select.databasesEnabled(catalogName));
        } else {
            return Result.success(select.databases(catalogName));
        }
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/list")
    public Result<?> tables(@PathVariable("catalogName") String catalogName,
                            @PathVariable("databaseName") String databaseName,
                            @RequestParam Boolean onlyEnabled) {
        if (onlyEnabled) {
            return Result.success(select.tablesEnabled(catalogName, databaseName));
        } else {
            return Result.success(select.tables(catalogName, databaseName));
        }
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/{tableName}/column/list")
    public Result<?> columns(@PathVariable("catalogName") String catalogName,
                             @PathVariable("databaseName") String databaseName,
                             @PathVariable("tableName") String tableName) {
        return Result.success(select.columns(catalogName, databaseName, tableName));
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/{tableName}/dimension/list")
    public Result<?> dimensions(@PathVariable("catalogName") String catalogName,
                                @PathVariable("databaseName") String databaseName,
                                @PathVariable("tableName") String tableName) {
        return Result.success(select.dimensions(catalogName, databaseName, tableName));
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/enabled")
    public Result<?> databaseEnabled(@PathVariable("catalogName") String catalogName,
                                     @PathVariable("databaseName") String databaseName,
                                     @RequestParam Boolean enabled) throws EntityNotFoundException {
        SourceDatabase database = select.databaseCheck(new SourceDatabaseKey(catalogName, databaseName));
        patch.databaseEnabled(database, enabled);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/{tableName}/enabled")
    public Result<?> tableEnabled(@PathVariable("catalogName") String catalogName,
                                  @PathVariable("databaseName") String databaseName,
                                  @PathVariable("tableName") String tableName,
                                  @RequestParam Boolean enabled) throws EntityNotFoundException {
        SourceTable table = select.tableCheck(new SourceTableKey(catalogName, databaseName, tableName));
        patch.tableEnabled(table, enabled);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/{tableName}/dimension/{dimensionName}/enabled")
    public Result<?> dimensions(@PathVariable("catalogName") String catalogName,
                                @PathVariable("databaseName") String databaseName,
                                @PathVariable("tableName") String tableName,
                                @PathVariable("dimensionName") String dimensionName) {
        patch.dimensions(catalogName, databaseName, tableName, dimensionName);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/{tableName}/dimension/add")
    public Result<?> saveDimensions(@PathVariable("catalogName") String catalogName,
                                    @PathVariable("databaseName") String databaseName,
                                    @PathVariable("tableName") String tableName,
                                    @RequestBody SourceDimensionSaveVo vo) throws EntityNotFoundException {
        SourceTable table = select.tableCheck(new SourceTableKey(catalogName, databaseName, tableName));
        sourceService.saveDimensions(catalogName, databaseName, tableName, vo);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/{tableName}/createDorisTable")
    public Result<String> createDorisTableScript(@PathVariable("catalogName") String catalogName,
                                                 @PathVariable("databaseName") String databaseName,
                                                 @PathVariable("tableName") String tableName) throws EntityNotFoundException {
        SourceTable table = select.tableCheck(new SourceTableKey(catalogName, databaseName, tableName));
        return Result.success(sourceService.createDorisTableScript(table));
    }
}