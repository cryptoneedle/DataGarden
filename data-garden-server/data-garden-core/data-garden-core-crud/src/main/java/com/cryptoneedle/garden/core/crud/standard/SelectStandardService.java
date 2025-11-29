package com.cryptoneedle.garden.core.crud.standard;


import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardColumn;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardTable;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardTableRepository;
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
public class SelectStandardService {

    private final StandardTableRepository standardTableRepository;
    private final StandardColumnRepository standardColumnRepository;

    public SelectStandardService(StandardTableRepository standardTableRepository,
                                 StandardColumnRepository standardColumnRepository) {
        this.standardTableRepository = standardTableRepository;
        this.standardColumnRepository = standardColumnRepository;
    }

    /**
     * StandardTable
     */
    public StandardTable table(DorisTableKey id) {
        return standardTableRepository.findById(id).orElse(null);
    }

    public StandardTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return standardTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("StandardTable", id.toString()));
    }

    public List<StandardTable> tables() {
        return standardTableRepository.tables();
    }

    public List<StandardTable> tables(String databaseName) {
        return standardTableRepository.tables(databaseName);
    }

    /**
     * StandardColumn
     */
    public StandardColumn column(DorisColumnKey id) {
        return standardColumnRepository.findById(id).orElse(null);
    }

    public StandardTable table(String tableName) {
        return standardTableRepository.table(tableName);
    }

    public StandardColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return standardColumnRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("StandardColumn", id.toString()));
    }

    public List<StandardColumn> columns() {
        return standardColumnRepository.columns();
    }

    public List<StandardColumn> columns(String tableName) {
        return standardColumnRepository.columns(tableName);
    }
}