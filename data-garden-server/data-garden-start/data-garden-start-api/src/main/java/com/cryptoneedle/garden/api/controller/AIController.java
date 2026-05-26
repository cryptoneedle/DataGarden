package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.core.ai.AIService;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.vo.dwd.DwdGenResultVo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-27
 */
@RestController
@RequestMapping("/ai")
public class AIController {
    
    public final AIService aiService;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public AIController(AIService aiService,
                        AddService add,
                        SelectService select,
                        SaveService save,
                        DeleteService delete,
                        PatchService patch) {
        this.aiService = aiService;
        this.add = add;
        this.select = select;
        this.save = save;
        this.delete = delete;
        this.patch = patch;
    }
    
    @PostMapping("/{odsTableName}/{columnName}/column/translate")
    public Result<?> translateOdsColumn(@PathVariable String odsTableName, @PathVariable String columnName) {
        aiService.translateOdsColumn(odsTableName, columnName);
        return Result.success();
    }
    
    @PostMapping("/{odsTableName}/dwd/generate")
    public Result<DwdGenResultVo> generateDwdTable(@PathVariable String odsTableName) {
        return Result.success(aiService.generateDwdTable(odsTableName));
    }
    
    @PostMapping("/data-lineage/dolphinScheduler")
    public Result<?> dolphinSchedulerDataLineage() {
        aiService.dolphinSchedulerDataLineage();
        return Result.success();
    }
}