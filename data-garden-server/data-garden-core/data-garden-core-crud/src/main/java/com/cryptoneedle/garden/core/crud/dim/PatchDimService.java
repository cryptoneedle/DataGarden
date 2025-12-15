package com.cryptoneedle.garden.core.crud.dim;


import com.cryptoneedle.garden.infrastructure.repository.dim.DimColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 部分更新数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchDimService {
    
    private final DimTableRepository dimTableRepository;
    private final DimColumnRepository dimColumnRepository;
    
    public PatchDimService(DimTableRepository dimTableRepository,
                           DimColumnRepository dimColumnRepository) {
        this.dimTableRepository = dimTableRepository;
        this.dimColumnRepository = dimColumnRepository;
    }
    
    /**
     * DimTable
     */
    
    /**
     * DimColumn
     */
}