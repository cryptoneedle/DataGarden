package com.cryptoneedle.garden.core.crud.dws;


import com.cryptoneedle.garden.infrastructure.entity.dws.DwsColumn;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsTable;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description: 新增数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class AddDwsService {

    private final SaveDwsService saveDwsService;
    private final DwsTableRepository dwsTableRepository;
    private final DwsColumnRepository dwsColumnRepository;

    public AddDwsService(SaveDwsService saveDwsService,
                         DwsTableRepository dwsTableRepository,
                         DwsColumnRepository dwsColumnRepository) {
        this.saveDwsService = saveDwsService;
        this.dwsTableRepository = dwsTableRepository;
        this.dwsColumnRepository = dwsColumnRepository;
    }

    /**
     * DwsTable
     */
    public void table(DwsTable entity) {
        saveDwsService.table(entity);
    }

    public void tables(List<DwsTable> list) {
        saveDwsService.tables(list);
    }

    /**
     * DwsColumn
     */
    public void column(DwsColumn entity) {
        saveDwsService.column(entity);
    }

    public void columns(List<DwsColumn> list) {
        saveDwsService.columns(list);
    }
}