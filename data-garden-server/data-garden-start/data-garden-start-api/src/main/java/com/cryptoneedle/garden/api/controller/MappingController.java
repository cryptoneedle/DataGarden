package com.cryptoneedle.garden.api.controller;


import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.core.crud.mapping.*;
import com.cryptoneedle.garden.core.mapping.MappingService;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumnRely;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTable;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTableRely;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/mapping")
public class MappingController {
    
    public final MappingService mappingService;
    public final AddMappingService add;
    public final SelectMappingService select;
    public final SaveMappingService save;
    public final DeleteMappingService delete;
    public final PatchMappingService patch;
    
    public MappingController(MappingService mappingService,
                             AddMappingService add,
                             SelectMappingService select,
                             SaveMappingService save,
                             DeleteMappingService delete,
                             PatchMappingService patch) {
        this.mappingService = mappingService;
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
    }
    
    @PostMapping("/table/list")
    public Result<List<MappingTable>> listTables() {
        return Result.success(select.tables());
    }
    
    @PostMapping("/column/list")
    public Result<List<MappingColumn>> listColumns() {
        return Result.success(select.columns());
    }
    
    @PostMapping("/{tableName}/column/list")
    public Result<List<MappingColumn>> listColumnsByTableName(@PathVariable String tableName) {
        return Result.success(select.columns(tableName));
    }
    
    @PostMapping("/{tableName}/column/{sourceTableName}/list")
    public Result<List<MappingColumnRely>> listColumnsByMapping(@PathVariable String tableName, @PathVariable String sourceTableName) {
        return Result.success(select.listColumnRelysByMapping(tableName, sourceTableName));
    }
    
    @PostMapping("/rely/table")
    public Result<?> relyTable(@RequestBody MappingTableRely mappingTableRely) {
        mappingService.relyTable(mappingTableRely);
        return Result.success();
    }
    
    @PostMapping("/rely/column")
    public Result<?> relyColumn(@RequestBody MappingColumnRely mappingColumnRely) {
        mappingService.relyColumn(mappingColumnRely);
        return Result.success();
    }
    
    @PostMapping("/unrely/table")
    public Result<?> unrelyTable(@RequestBody MappingTableRely mappingTableRely) {
        mappingService.unrelyTable(mappingTableRely);
        return Result.success();
    }
    
    @PostMapping("/unrely/column")
    public Result<?> unrelyColumn(@RequestBody MappingColumnRely mappingColumnRely) {
        mappingService.unrelyColumn(mappingColumnRely);
        return Result.success();
    }
}