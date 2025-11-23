package com.cryptoneedle.garden.core.crud.dwd;


import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdColumn;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdTable;
import com.cryptoneedle.garden.infrastructure.repository.dwd.DwdColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dwd.DwdTableRepository;
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
public class SelectDwdService {

    private final DwdTableRepository dwdTableRepository;
    private final DwdColumnRepository dwdColumnRepository;

    public SelectDwdService(DwdTableRepository dwdTableRepository,
                            DwdColumnRepository dwdColumnRepository) {
        this.dwdTableRepository = dwdTableRepository;
        this.dwdColumnRepository = dwdColumnRepository;
    }

    /**
     * DwdTable
     */
    public DwdTable table(DorisTableKey id) {
        return dwdTableRepository.findById(id).orElse(null);
    }

    public DwdTable table(String tableName) {
        return dwdTableRepository.table(tableName);
    }

    public DwdTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return dwdTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DwdTable", id.toString()));
    }

    public List<DwdTable> tables() {
        return dwdTableRepository.tables();
    }

    public List<DwdTable> tables(String databaseName) {
        return dwdTableRepository.tables(databaseName);
    }

    /**
     * DwdColumn
     */
    public DwdColumn column(DorisColumnKey id) {
        return dwdColumnRepository.findById(id).orElse(null);
    }

    public DwdColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return dwdColumnRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DwdColumn", id.toString()));
    }

    public List<DwdColumn> columns() {
        return dwdColumnRepository.columns();
    }

    public List<DwdColumn> columns(String tableName) {
        return dwdColumnRepository.columns(tableName);
    }
}