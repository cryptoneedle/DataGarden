package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.core.crud.source.*;
import com.cryptoneedle.garden.core.source.SourceCatalogService;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDatabase;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceTable;
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
    
    private final SourceCatalogService sourceCatalogService;
    public final AddSourceService add;
    public final SelectSourceService select;
    public final SaveSourceService save;
    public final DeleteSourceService delete;
    public final PatchSourceService patch;
    
    public SourceController(SourceCatalogService sourceCatalogService,
                            AddSourceService add,
                            SelectSourceService select,
                            SaveSourceService save,
                            DeleteSourceService delete,
                            PatchSourceService patch) {
        this.sourceCatalogService = sourceCatalogService;
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
    }
    
    @PostMapping("/catalog/test/server")
    public Result<?> testAddCatalogServer(@Valid @RequestBody SourceCatalogSaveVo vo) {
        return Result.success(sourceCatalogService.testServer(vo.sourceCatalog(), vo.isNeedStore()));
    }
    
    @PostMapping("/catalog/test/jdbc")
    public Result<?> testAddCatalogJdbc(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceCatalogService.fillPassword(vo);
        return Result.success(sourceCatalogService.testJdbc(vo.sourceCatalog(), vo.isNeedStore()));
    }
    
    @PostMapping("/catalog/test/doris")
    public Result<?> testAddCatalogDoris(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceCatalogService.fillPassword(vo);
        return Result.success(sourceCatalogService.testDoris(vo.sourceCatalog(), vo.isNeedStore()));
    }
    
    @PostMapping("/catalog/add")
    public Result<?> addCatalog(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceCatalogService.addVo(vo);
        return Result.success();
    }
    
    @PostMapping("/catalog/save")
    public Result<?> saveCatalog(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceCatalogService.fillPassword(vo);
        sourceCatalogService.saveVo(vo);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/sync")
    public Result<?> syncCatalog(@PathVariable("catalogName") String catalogName) throws EntityNotFoundException {
        SourceCatalog catalog = select.catalogCheck(new SourceCatalogKey(catalogName));
        sourceCatalogService.syncCatalog(catalog);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/sync")
    public Result<?> syncCatalog(@PathVariable("catalogName") String catalogName,
                                 @PathVariable("databaseName") String databaseName) throws EntityNotFoundException {
        SourceCatalog catalog = select.catalogCheck(new SourceCatalogKey(catalogName));
        SourceDatabase database = select.databaseCheck(new SourceDatabaseKey(catalogName, databaseName));
        sourceCatalogService.syncDatabase(catalog, database);
        return Result.success();
    }
    
    @PostMapping("/catalog/{catalogName}/database/{databaseName}/table/{tableName}/sync")
    public Result<?> syncCatalog(@PathVariable("catalogName") String catalogName,
                                 @PathVariable("databaseName") String databaseName,
                                 @PathVariable("tableName") String tableName) throws EntityNotFoundException {
        SourceCatalog catalog = select.catalogCheck(new SourceCatalogKey(catalogName));
        SourceDatabase database = select.databaseCheck(new SourceDatabaseKey(catalogName, databaseName));
        SourceTable table = select.tableCheck(new SourceTableKey(catalogName, databaseName, tableName));
        sourceCatalogService.syncTable(catalog, database, table);
        return Result.success();
    }
}