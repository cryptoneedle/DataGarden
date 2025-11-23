package com.cryptoneedle.garden.core.crud.dim;


import com.cryptoneedle.garden.infrastructure.entity.dim.DimColumn;
import com.cryptoneedle.garden.infrastructure.entity.dim.DimTable;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description: 新增数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class AddDimService {

    private final SaveDimService saveDimService;
    private final DimTableRepository dimTableRepository;
    private final DimColumnRepository dimColumnRepository;

    public AddDimService(SaveDimService saveDimService,
                         DimTableRepository dimTableRepository,
                         DimColumnRepository dimColumnRepository) {
        this.saveDimService = saveDimService;
        this.dimTableRepository = dimTableRepository;
        this.dimColumnRepository = dimColumnRepository;
    }

    /**
     * DimTable
     */
    public void table(DimTable entity) {
        saveDimService.table(entity);
    }

    public void tables(List<DimTable> list) {
        saveDimService.tables(list);
    }

    /**
     * DimColumn
     */
    public void column(DimColumn entity) {
        saveDimService.column(entity);
    }

    public void columns(List<DimColumn> list) {
        saveDimService.columns(list);
    }
}