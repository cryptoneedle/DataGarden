package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.core.crud.dwd.*;
import com.cryptoneedle.garden.core.dwd.DwdService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-14
 */
@RestController
@RequestMapping("/dwd")
public class DwdController {
    
    public final DwdService dwdService;
    public final AddDwdService add;
    public final SelectDwdService select;
    public final SaveDwdService save;
    public final DeleteDwdService delete;
    public final PatchDwdService patch;
    
    public DwdController(DwdService dwdService,
                         AddDwdService add,
                         SelectDwdService select,
                         SaveDwdService save,
                         DeleteDwdService delete,
                         PatchDwdService patch) {
        this.dwdService = dwdService;
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
    }
    
    @PostMapping("/parseLogicToCreateSql/export")
    public Result<Void> exportParseLogicToCreateSql(@RequestParam("file") MultipartFile file) throws IOException {
        dwdService.parseLogicToCreateSql(file);
        return Result.success();
    }
}