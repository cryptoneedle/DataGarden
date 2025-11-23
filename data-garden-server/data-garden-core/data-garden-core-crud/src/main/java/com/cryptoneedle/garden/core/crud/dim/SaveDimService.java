package com.cryptoneedle.garden.core.crud.dim;


import com.cryptoneedle.garden.infrastructure.entity.dim.DimColumn;
import com.cryptoneedle.garden.infrastructure.entity.dim.DimTable;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description: 保存数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class SaveDimService {

    private final DimTableRepository dimTableRepository;
    private final DimColumnRepository dimColumnRepository;

    public SaveDimService(DimTableRepository dimTableRepository,
                          DimColumnRepository dimColumnRepository) {
        this.dimTableRepository = dimTableRepository;
        this.dimColumnRepository = dimColumnRepository;
    }

    /**
     * DimTable
     */
    public void table(DimTable entity) {
        dimTableRepository.save(entity);
    }

    public void tables(List<DimTable> list) {
        dimTableRepository.saveAll(list);
    }

    /**
     * DimColumn
     */
    public void column(DimColumn entity) {
        dimColumnRepository.save(entity);
    }

    public void columns(List<DimColumn> list) {
        dimColumnRepository.saveAll(list);
    }
}