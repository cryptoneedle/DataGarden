package com.cryptoneedle.garden.core.crud.dws;


import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsColumn;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsTable;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsTableRepository;
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
@Transactional(readOnly = true)
public class SelectDwsService {

    private final DwsTableRepository dwsTableRepository;
    private final DwsColumnRepository dwsColumnRepository;

    public SelectDwsService(DwsTableRepository dwsTableRepository,
                            DwsColumnRepository dwsColumnRepository) {
        this.dwsTableRepository = dwsTableRepository;
        this.dwsColumnRepository = dwsColumnRepository;
    }

    /**
     * DwsTable
     */
    public DwsTable table(DorisTableKey id) {
        return dwsTableRepository.findById(id).orElse(null);
    }

    public DwsTable table(String tableName) {
        return dwsTableRepository.table(tableName);
    }

    public DwsTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return dwsTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DwsTable", id.toString()));
    }

    public List<DwsTable> tables() {
        return dwsTableRepository.tables();
    }

    public List<DwsTable> tables(String databaseName) {
        return dwsTableRepository.tables(databaseName);
    }

    /**
     * DwsColumn
     */
    public DwsColumn column(DorisColumnKey id) {
        return dwsColumnRepository.findById(id).orElse(null);
    }

    public DwsColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return dwsColumnRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DwsColumn", id.toString()));
    }

    public List<DwsColumn> columns() {
        return dwsColumnRepository.columns();
    }

    public List<DwsColumn> columns(String tableName) {
        return dwsColumnRepository.columns(tableName);
    }
}