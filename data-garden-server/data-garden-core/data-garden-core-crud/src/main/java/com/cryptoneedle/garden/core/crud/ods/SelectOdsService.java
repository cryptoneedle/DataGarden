package com.cryptoneedle.garden.core.crud.ods;


import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsTableRepository;
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
public class SelectOdsService {

    private final OdsTableRepository odsTableRepository;
    private final OdsColumnRepository odsColumnRepository;

    public SelectOdsService(OdsTableRepository odsTableRepository,
                            OdsColumnRepository odsColumnRepository) {
        this.odsTableRepository = odsTableRepository;
        this.odsColumnRepository = odsColumnRepository;
    }

    /**
     * OdsTable
     */
    public OdsTable table(DorisTableKey id) {
        return odsTableRepository.findById(id).orElse(null);
    }

    public OdsTable table(String tableName) {
        return odsTableRepository.table(tableName);
    }

    public OdsTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return odsTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("OdsTable", id.toString()));
    }

    public List<OdsTable> tables() {
        return odsTableRepository.tables();
    }

    public List<OdsTable> tables(String databaseName) {
        return odsTableRepository.tables(databaseName);
    }

    /**
     * OdsColumn
     */
    public OdsColumn column(DorisColumnKey id) {
        return odsColumnRepository.findById(id).orElse(null);
    }

    public OdsColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return odsColumnRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("OdsColumn", id.toString()));
    }

    public List<OdsColumn> columns() {
        return odsColumnRepository.columns();
    }

    public List<OdsColumn> columns(String tableName) {
        return odsColumnRepository.columns(tableName);
    }
}