package com.cryptoneedle.garden.core.crud.standard;


import com.cryptoneedle.garden.infrastructure.entity.standard.StandardColumn;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardTable;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 保存数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SaveStandardService {
    
    private final StandardTableRepository standardTableRepository;
    private final StandardColumnRepository standardColumnRepository;
    
    public SaveStandardService(StandardTableRepository standardTableRepository,
                               StandardColumnRepository standardColumnRepository) {
        this.standardTableRepository = standardTableRepository;
        this.standardColumnRepository = standardColumnRepository;
    }
    
    /**
     * StandardTable
     */
    public void table(StandardTable entity) {
        standardTableRepository.save(entity);
    }
    
    public void tables(List<StandardTable> list) {
        standardTableRepository.saveAll(list);
    }
    
    /**
     * StandardColumn
     */
    public void column(StandardColumn entity) {
        standardColumnRepository.save(entity);
    }
    
    public void columns(List<StandardColumn> list) {
        standardColumnRepository.saveAll(list);
    }
}