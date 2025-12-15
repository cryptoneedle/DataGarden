package com.cryptoneedle.garden.core.crud.ods;


import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsTableRepository;
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
public class DeleteOdsService {
    
    private final OdsTableRepository odsTableRepository;
    private final OdsColumnRepository odsColumnRepository;
    
    public DeleteOdsService(OdsTableRepository odsTableRepository,
                            OdsColumnRepository odsColumnRepository) {
        this.odsTableRepository = odsTableRepository;
        this.odsColumnRepository = odsColumnRepository;
    }
    
    /**
     * OdsTable
     */
    public void table(OdsTable entity) {
        odsTableRepository.delete(entity);
    }
    
    public void tables(List<OdsTable> list) {
        odsTableRepository.deleteAll(list);
    }
    
    /**
     * OdsColumn
     */
    public void column(OdsColumn entity) {
        odsColumnRepository.delete(entity);
    }
    
    public void columns(List<OdsColumn> list) {
        odsColumnRepository.deleteAll(list);
    }
}