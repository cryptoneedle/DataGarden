package com.cryptoneedle.garden.api.controller;

import com.bubbles.engine.common.core.result.Result;
import com.cryptoneedle.garden.core.crud.source.*;
import com.cryptoneedle.garden.core.source.SourceCatalogService;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceCatalogSaveVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    @PostMapping("/catalog/add/test/server")
    public Result<?> testAddCatalogServer(@Valid @RequestBody SourceCatalogSaveVo vo) {
        return Result.success(sourceCatalogService.testServer(vo.sourceCatalog(), false));
    }
    
    @PostMapping("/catalog/add/test/jdbc")
    public Result<?> testAddCatalogJdbc(@Valid @RequestBody SourceCatalogSaveVo vo) {
        return Result.success(sourceCatalogService.testJdbc(vo.sourceCatalog(), false));
    }
    
    @PostMapping("/catalog/add/test/doris")
    public Result<?> testAddCatalogDoris(@Valid @RequestBody SourceCatalogSaveVo vo) {
        return Result.success(sourceCatalogService.testDoris(vo.sourceCatalog(), false));
    }
    
    @PostMapping("/catalog/add")
    public Result<?> addCatalog(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceCatalogService.addVo(vo);
        return Result.success();
    }
    
    @PostMapping("/catalog/save/test/server")
    public Result<?> testSaveCatalogServer(@Valid @RequestBody SourceCatalogSaveVo vo) {
        return Result.success(sourceCatalogService.testServer(vo.sourceCatalog(), false));
    }
    
    @PostMapping("/catalog/save/test/jdbc")
    public Result<?> testSaveCatalogJdbc(@Valid @RequestBody SourceCatalogSaveVo vo) {
        SourceCatalog catalog = vo.sourceCatalog();
        if (vo.getPassword() == null) {
            SourceCatalog old = select.catalog(vo.sourceCatalogKey());
            catalog.setPassword(old.getPassword());
        }
        return Result.success(sourceCatalogService.testJdbc(catalog, false));
    }
    
    @PostMapping("/catalog/save/test/doris")
    public Result<?> testSaveCatalogDoris(@Valid @RequestBody SourceCatalogSaveVo vo) {
        SourceCatalog catalog = vo.sourceCatalog();
        if (vo.getPassword() == null) {
            SourceCatalog old = select.catalog(vo.sourceCatalogKey());
            catalog.setPassword(old.getPassword());
        }
        return Result.success(sourceCatalogService.testDoris(catalog, false));
    }
    
    @PostMapping("/catalog/save")
    public Result<?> saveCatalog(@Valid @RequestBody SourceCatalogSaveVo vo) {
        sourceCatalogService.saveVo(vo);
        return Result.success();
    }
}