package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.core.crud.source.*;
import com.cryptoneedle.garden.core.ds.DolphinSchedulerService;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDatabase;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceTable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-27
 */
@RestController
@RequestMapping("/dolphin-scheduler")
public class DolphinSchedulerController {
    
    public final AddSourceService add;
    public final SelectSourceService select;
    public final SaveSourceService save;
    public final DeleteSourceService delete;
    public final PatchSourceService patch;
    private final DolphinSchedulerService dolphinSchedulerService;
    
    public DolphinSchedulerController(AddSourceService add,
                                      SelectSourceService select,
                                      SaveSourceService save,
                                      DeleteSourceService delete,
                                      PatchSourceService patch,
                                      DolphinSchedulerService dolphinSchedulerService) {
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
        this.dolphinSchedulerService = dolphinSchedulerService;
    }
    
    @PostMapping("/source/catalog/{catalogName}/database/{databaseName}/generateSeatunnelTask")
    public Result<?> generateSeatunnelTaskBatch(@PathVariable("catalogName") String catalogName,
                                                @PathVariable("databaseName") String databaseName) throws EntityNotFoundException {
        SourceCatalog catalog = select.catalogCheck(new SourceCatalogKey(catalogName));
        SourceDatabase database = select.databaseCheck(new SourceDatabaseKey(catalogName, databaseName));
        dolphinSchedulerService.dealFullTask(catalog, database);
        dolphinSchedulerService.dealIncrementTask(catalog, database);
        return Result.success();
    }
    
    @PostMapping("/source/catalog/{catalogName}/database/{databaseName}/table/{tableName}/generateSeatunnelTask")
    public Result<Object> generateSeatunnelTask(@PathVariable("catalogName") String catalogName,
                                                @PathVariable("databaseName") String databaseName,
                                                @PathVariable("tableName") String tableName) throws EntityNotFoundException {
        SourceCatalog catalog = select.catalogCheck(new SourceCatalogKey(catalogName));
        SourceDatabase database = select.databaseCheck(new SourceDatabaseKey(catalogName, databaseName));
        SourceTable table = select.tableCheck(new SourceTableKey(catalogName, databaseName, tableName));
        dolphinSchedulerService.dealFullTask(catalog, database, table);
        
        SourceCollectFrequencyType collectFrequency = table.getCollectFrequency();
        Integer collectTimePoint = table.getCollectTimePoint();
        Integer collectGroupNum = table.getCollectGroupNum();
        // 查询出同一组的表
        List<SourceTable> tables = select.tablesByCollect(table.getId().getCatalogName(), table.getId().getDatabaseName(), collectFrequency, collectTimePoint, collectGroupNum);
        dolphinSchedulerService.dealIncrementTask(catalog, database, collectFrequency, collectTimePoint, collectGroupNum, tables);
        return Result.success();
    }
}