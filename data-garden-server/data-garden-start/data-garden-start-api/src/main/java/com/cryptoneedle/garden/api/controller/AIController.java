package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.common.key.source.SourceDatabaseKey;
import com.cryptoneedle.garden.common.key.source.SourceTableKey;
import com.cryptoneedle.garden.core.ai.AIService;
import com.cryptoneedle.garden.core.crud.*;
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
    
    @PostMapping("/{tableName}/{columnName}/column/translate")
    public Result<?> translateOdsColumn(@PathVariable String tableName, @PathVariable String columnName) {
        aiService.translateOdsColumn(tableName, columnName);
        return Result.success();
    }
}