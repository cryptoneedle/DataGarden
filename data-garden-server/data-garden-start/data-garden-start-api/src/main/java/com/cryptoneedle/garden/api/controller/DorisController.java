package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisDatabaseKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.core.crud.doris.*;
import com.cryptoneedle.garden.core.doris.DorisService;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisDatabase;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/doris")
public class DorisController {
    
    public final DorisService dorisService;
    public final AddDorisService add;
    public final SelectDorisService select;
    public final SaveDorisService save;
    public final DeleteDorisService delete;
    public final PatchDorisService patch;
    
    public DorisController(DorisService dorisService,
                           AddDorisService add,
                           SelectDorisService select,
                           SaveDorisService save,
                           DeleteDorisService delete,
                           PatchDorisService patch) {
        this.dorisService = dorisService;
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
    }
    
    @PostMapping("/catalog/sync")
    public Result<?> syncCatalog() {
        dorisService.syncCatalog();
        return Result.success();
    }
    
    @PostMapping("/catalog/database/{databaseName}/sync")
    public Result<?> syncDatabase(@PathVariable("databaseName") String databaseName) throws EntityNotFoundException {
        DorisDatabase database = select.databaseCheck(new DorisDatabaseKey(databaseName));
        dorisService.syncDatabase(database, null);
        return Result.success();
    }
    
    @PostMapping("/catalog/database/{databaseName}/table/{tableName}/sync")
    public Result<?> syncTable(@PathVariable("databaseName") String databaseName,
                               @PathVariable("tableName") String tableName) throws EntityNotFoundException {
        DorisDatabase database = select.databaseCheck(new DorisDatabaseKey(databaseName));
        DorisTable table = select.tableCheck(new DorisTableKey(databaseName, tableName));
        dorisService.syncTable(database, table);
        return Result.success();
    }
    
    @PostMapping("/catalog/fixCreateTable")
    public Result<?> fixCreateTable() {
        dorisService.fixCreateTable();
        return Result.success();
    }
}