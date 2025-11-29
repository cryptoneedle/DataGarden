package com.cryptoneedle.garden.core.crud.dim;


import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.dim.DimColumn;
import com.cryptoneedle.garden.infrastructure.entity.dim.DimTable;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dim.DimTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 查询数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SelectDimService {

    private final DimTableRepository dimTableRepository;
    private final DimColumnRepository dimColumnRepository;

    public SelectDimService(DimTableRepository dimTableRepository,
                            DimColumnRepository dimColumnRepository) {
        this.dimTableRepository = dimTableRepository;
        this.dimColumnRepository = dimColumnRepository;
    }

    /**
     * DimTable
     */
    public DimTable table(DorisTableKey id) {
        return dimTableRepository.findById(id).orElse(null);
    }

    public DimTable table(String tableName) {
        return dimTableRepository.table(tableName);
    }

    public DimTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return dimTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DimTable", id.toString()));
    }

    public List<DimTable> tables() {
        return dimTableRepository.tables();
    }

    public List<DimTable> tables(String databaseName) {
        return dimTableRepository.tables(databaseName);
    }

    /**
     * DimColumn
     */
    public DimColumn column(DorisColumnKey id) {
        return dimColumnRepository.findById(id).orElse(null);
    }

    public DimColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return dimColumnRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DimColumn", id.toString()));
    }

    public List<DimColumn> columns() {
        return dimColumnRepository.columns();
    }

    public List<DimColumn> columns(String tableName) {
        return dimColumnRepository.columns(tableName);
    }
}