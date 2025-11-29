package com.cryptoneedle.garden.core.crud.dwd;


import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdColumn;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdTable;
import com.cryptoneedle.garden.infrastructure.repository.dwd.DwdColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dwd.DwdTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 新增数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class AddDwdService {

    private final SaveDwdService saveDwdService;
    private final DwdTableRepository dwdTableRepository;
    private final DwdColumnRepository dwdColumnRepository;

    public AddDwdService(SaveDwdService saveDwdService,
                         DwdTableRepository dwdTableRepository,
                         DwdColumnRepository dwdColumnRepository) {
        this.saveDwdService = saveDwdService;
        this.dwdTableRepository = dwdTableRepository;
        this.dwdColumnRepository = dwdColumnRepository;
    }

    /**
     * DwdTable
     */
    public void table(DwdTable entity) {
        saveDwdService.table(entity);
    }

    public void tables(List<DwdTable> list) {
        saveDwdService.tables(list);
    }

    /**
     * DwdColumn
     */
    public void column(DwdColumn entity) {
        saveDwdService.column(entity);
    }

    public void columns(List<DwdColumn> list) {
        saveDwdService.columns(list);
    }
}