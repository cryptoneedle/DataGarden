package com.cryptoneedle.garden.core.crud.mapping;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 部分更新数据映射层(MAPPING)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchMappingService {
    
    private final SelectMappingService select;
    private final SaveMappingService save;
    
    public PatchMappingService(SelectMappingService selectMappingService,
                               SaveMappingService saveMappingService) {
        this.select = selectMappingService;
        this.save = saveMappingService;
    }
    
    /**
     * MappingTable
     */
    
    /**
     * MappingColumn
     */
}