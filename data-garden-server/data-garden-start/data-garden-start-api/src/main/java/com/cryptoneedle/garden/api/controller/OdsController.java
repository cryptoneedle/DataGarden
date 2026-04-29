package com.cryptoneedle.garden.api.controller;


import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.core.crud.ods.*;
import com.cryptoneedle.garden.core.ods.OdsService;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumnTranslate;
import com.cryptoneedle.garden.infrastructure.vo.ods.OdsColumnVo;
import com.cryptoneedle.garden.infrastructure.vo.ods.OdsTableVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/ods")
public class OdsController {
    
    public final OdsService service;
    public final AddOdsService add;
    public final SelectOdsService select;
    public final SaveOdsService save;
    public final DeleteOdsService delete;
    public final PatchOdsService patch;
    
    public OdsController(OdsService service,
                         AddOdsService add,
                         SelectOdsService select,
                         SaveOdsService save,
                         DeleteOdsService delete,
                         PatchOdsService patch) {
        this.service = service;
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
    }
    
    @PostMapping("/table/list")
    public Result<Page<OdsTableVo>> tableVos(@PageableDefault Pageable pageable, @RequestParam(required = false) String tableName) {
        return Result.success(service.tableVos(pageable, tableName));
    }
    
    @PostMapping("/{tableName}/column/list")
    public Result<List<OdsColumnVo>> columnVos(@PathVariable String tableName) {
        return Result.success(service.columnVos(tableName));
    }
    
    @PostMapping("/{tableName}/{columnName}/column/translate/list")
    public Result<List<OdsColumnTranslate>> translateColumnList(@PathVariable String tableName, @PathVariable String columnName) {
        return Result.success(service.translateColumnList(tableName, columnName));
    }
    
    @PostMapping("/{tableName}/{columnName}/column/translate/save")
    public Result<?> saveTranslateColumnList(@PathVariable String tableName, @PathVariable String columnName, @RequestBody List<OdsColumnTranslate> odsColumnTranslateList) {
        service.saveTranslateColumnList(tableName, columnName, odsColumnTranslateList);
        return Result.success();
    }
}