package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.core.crud.standard.*;
import com.cryptoneedle.garden.core.standard.StandardService;
import com.cryptoneedle.garden.infrastructure.vo.standard.StandardSaveVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/standard")
public class StandardController {
    
    public final StandardService standardService;
    public final AddStandardService add;
    public final SelectStandardService select;
    public final SaveStandardService save;
    public final DeleteStandardService delete;
    public final PatchStandardService patch;
    
    public StandardController(StandardService standardService,
                              AddStandardService add,
                              SelectStandardService select,
                              SaveStandardService save,
                              DeleteStandardService delete,
                              PatchStandardService patch) {
        this.standardService = standardService;
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
    }
    
    @PostMapping("/build")
    public Result<?> saveStandard(@RequestBody StandardSaveVo vo) {
        standardService.saveStandard(vo);
        return Result.success();
    }
}