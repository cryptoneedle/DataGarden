package com.cryptoneedle.garden.core.crud.mapping;


import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.common.key.doris.MappingTableRelyKey;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumnRely;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTable;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTableRely;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingColumnRelyRepository;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingTableRelyRepository;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 删除数据映射层(MAPPING)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DeleteMappingService {
    
    private final MappingTableRepository mappingTableRepository;
    private final MappingColumnRepository mappingColumnRepository;
    private final MappingTableRelyRepository mappingTableRelyRepository;
    private final MappingColumnRelyRepository mappingColumnRelyRepository;
    
    public DeleteMappingService(MappingTableRepository mappingTableRepository,
                                MappingColumnRepository mappingColumnRepository,
                                MappingTableRelyRepository mappingTableRelyRepository,
                                MappingColumnRelyRepository mappingColumnRelyRepository) {
        this.mappingTableRepository = mappingTableRepository;
        this.mappingColumnRepository = mappingColumnRepository;
        this.mappingTableRelyRepository = mappingTableRelyRepository;
        this.mappingColumnRelyRepository = mappingColumnRelyRepository;
    }
    
    /**
     * MappingTable
     */
    public void table(DorisTableKey key) {
        mappingTableRepository.deleteById(key);
    }
    
    public void table(MappingTable entity) {
        mappingTableRepository.delete(entity);
    }
    
    public void tables(List<MappingTable> list) {
        mappingTableRepository.deleteAll(list);
    }
    
    /**
     * MappingColumn
     */
    public void column(DorisColumnKey key) {
        mappingColumnRepository.deleteById(key);
    }
    
    public void column(MappingColumn entity) {
        mappingColumnRepository.delete(entity);
    }
    
    public void columns(List<MappingColumn> list) {
        mappingColumnRepository.deleteAll(list);
    }
    
    /**
     * MappingTableRely
     */
    public void tableRely(MappingTableRely entity) {
        mappingTableRelyRepository.delete(entity);
    }
    
    /**
     * MappingColumnRely
     */
    public void columnRely(MappingColumnRely entity) {
        mappingColumnRelyRepository.delete(entity);
    }
    
    public void columnRelyByTable(MappingTableRely mappingTableRely) {
        MappingTableRelyKey id = mappingTableRely.getId();
        mappingColumnRelyRepository.deleteByMappingTableRely(id.getMappingDatabaseName(), id.getMappingTableName(), id.getSourceDatabaseName(), id.getSourceTableName());
    }
}