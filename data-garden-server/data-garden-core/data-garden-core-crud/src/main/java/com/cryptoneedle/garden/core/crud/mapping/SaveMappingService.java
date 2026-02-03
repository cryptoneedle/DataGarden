package com.cryptoneedle.garden.core.crud.mapping;


import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTable;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 保存数据映射层(MAPPING)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SaveMappingService {
    
    private final MappingTableRepository mappingTableRepository;
    private final MappingColumnRepository mappingColumnRepository;
    
    public SaveMappingService(MappingTableRepository mappingTableRepository,
                              MappingColumnRepository mappingColumnRepository) {
        this.mappingTableRepository = mappingTableRepository;
        this.mappingColumnRepository = mappingColumnRepository;
    }
    
    /**
     * MappingTable
     */
    public void table(MappingTable entity) {
        mappingTableRepository.save(entity);
    }
    
    public void tables(List<MappingTable> list) {
        mappingTableRepository.saveAll(list);
    }
    
    /**
     * MappingColumn
     */
    public void column(MappingColumn entity) {
        mappingColumnRepository.save(entity);
    }
    
    public void columns(List<MappingColumn> list) {
        mappingColumnRepository.saveAll(list);
    }
}