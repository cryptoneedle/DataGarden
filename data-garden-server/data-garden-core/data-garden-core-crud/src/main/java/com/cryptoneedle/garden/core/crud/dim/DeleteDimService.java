package com.cryptoneedle.garden.core.crud.dim;


import com.cryptoneedle.garden.infrastructure.entity.dim.DimColumn;
import com.cryptoneedle.garden.infrastructure.entity.dim.DimTable;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimTableRepository;
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
public class DeleteDimService {

    private final DimTableRepository dimTableRepository;
    private final DimColumnRepository dimColumnRepository;

    public DeleteDimService(DimTableRepository dimTableRepository,
                            DimColumnRepository dimColumnRepository) {
        this.dimTableRepository = dimTableRepository;
        this.dimColumnRepository = dimColumnRepository;
    }

    /**
     * DimTable
     */
    public void table(DimTable entity) {
        dimTableRepository.delete(entity);
    }

    public void tables(List<DimTable> list) {
        dimTableRepository.deleteAll(list);
    }

    /**
     * DimColumn
     */
    public void column(DimColumn entity) {
        dimColumnRepository.delete(entity);
    }

    public void columns(List<DimColumn> list) {
        dimColumnRepository.deleteAll(list);
    }
}