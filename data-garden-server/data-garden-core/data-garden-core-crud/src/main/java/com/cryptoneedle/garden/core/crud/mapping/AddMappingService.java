package com.cryptoneedle.garden.core.crud.mapping;


import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 新增数据映射层(MAPPING)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class AddMappingService {
    
    private final SelectMappingService select;
    private final SaveMappingService save;
    
    public AddMappingService(SelectMappingService selectMappingService,
                             SaveMappingService saveMappingService) {
        this.select = selectMappingService;
        this.save = saveMappingService;
    }
    
    /**
     * MappingTable
     */
    public void table(MappingTable entity) {
        save.table(entity);
    }
    
    public void tables(List<MappingTable> list) {
        save.tables(list);
    }
    
    /**
     * MappingColumn
     */
    public void column(MappingColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<MappingColumn> list) {
        save.columns(list);
    }
}