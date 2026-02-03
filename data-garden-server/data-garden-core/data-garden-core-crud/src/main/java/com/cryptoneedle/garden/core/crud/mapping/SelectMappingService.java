package com.cryptoneedle.garden.core.crud.mapping;


import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTable;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 查询数据映射层(MAPPING)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SelectMappingService {
    
    private final MappingTableRepository mappingTableRepository;
    private final MappingColumnRepository mappingColumnRepository;
    
    public SelectMappingService(MappingTableRepository mappingTableRepository,
                                MappingColumnRepository mappingColumnRepository) {
        this.mappingTableRepository = mappingTableRepository;
        this.mappingColumnRepository = mappingColumnRepository;
    }
    
    /**
     * MappingTable
     */
    public MappingTable table(DorisTableKey id) {
        return mappingTableRepository.findById(id).orElse(null);
    }
    
    public MappingTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return mappingTableRepository.findById(id)
                                      .orElseThrow(() -> new EntityNotFoundException("MappingTable", id.toString()));
    }
    
    public List<MappingTable> tables() {
        return mappingTableRepository.tables();
    }
    
    public List<MappingTable> tables(String databaseName) {
        return mappingTableRepository.tables(databaseName);
    }
    
    /**
     * MappingColumn
     */
    public MappingColumn column(DorisColumnKey id) {
        return mappingColumnRepository.findById(id).orElse(null);
    }
    
    public MappingTable table(String tableName) {
        return mappingTableRepository.table(tableName);
    }
    
    public MappingColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return mappingColumnRepository.findById(id)
                                       .orElseThrow(() -> new EntityNotFoundException("MappingColumn", id.toString()));
    }
    
    public List<MappingColumn> columns() {
        return mappingColumnRepository.columns();
    }
    
    public List<MappingColumn> columns(String tableName) {
        return mappingColumnRepository.columns(tableName);
    }
}