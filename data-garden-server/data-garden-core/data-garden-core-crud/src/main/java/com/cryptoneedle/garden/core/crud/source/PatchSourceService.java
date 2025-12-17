package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 部分更新数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchSourceService {
    
    private final SelectConfigService selectConfigService;
    private final SelectSourceService select;
    private final SaveSourceService save;
    
    public PatchSourceService(SelectConfigService selectConfigService,
                            SelectSourceService selectSourceService,
                            SaveSourceService saveSourceService) {
        this.selectConfigService = selectConfigService;
        this.select = selectSourceService;
        this.save = saveSourceService;
    }
    
    /**
     * SourceCatalog
     */
    
    /**
     * SourceDatabase
     */
    
    /**
     * SourceTable
     */
    
    /**
     * SourceColumn
     */
    
    /**
     * SourceDimension
     */
}