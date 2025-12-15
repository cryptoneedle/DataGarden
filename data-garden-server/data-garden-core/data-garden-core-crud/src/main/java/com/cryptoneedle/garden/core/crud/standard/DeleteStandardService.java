package com.cryptoneedle.garden.core.crud.standard;


import com.cryptoneedle.garden.infrastructure.entity.standard.StandardColumn;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardTable;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 删除数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DeleteStandardService {
    
    private final StandardTableRepository standardTableRepository;
    private final StandardColumnRepository standardColumnRepository;
    
    public DeleteStandardService(StandardTableRepository standardTableRepository,
                                 StandardColumnRepository standardColumnRepository) {
        this.standardTableRepository = standardTableRepository;
        this.standardColumnRepository = standardColumnRepository;
    }
    
    /**
     * StandardTable
     */
    public void table(StandardTable entity) {
        standardTableRepository.delete(entity);
    }
    
    public void tables(List<StandardTable> list) {
        standardTableRepository.deleteAll(list);
    }
    
    /**
     * StandardColumn
     */
    public void column(StandardColumn entity) {
        standardColumnRepository.delete(entity);
    }
    
    public void columns(List<StandardColumn> list) {
        standardColumnRepository.deleteAll(list);
    }
}