package com.cryptoneedle.garden.core.crud.dws;


import com.cryptoneedle.garden.infrastructure.entity.dws.DwsColumn;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsTable;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description: 保存数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class SaveDwsService {

    private final DwsTableRepository dwsTableRepository;
    private final DwsColumnRepository dwsColumnRepository;

    public SaveDwsService(DwsTableRepository dwsTableRepository,
                          DwsColumnRepository dwsColumnRepository) {
        this.dwsTableRepository = dwsTableRepository;
        this.dwsColumnRepository = dwsColumnRepository;
    }

    /**
     * DwsTable
     */
    public void table(DwsTable entity) {
        dwsTableRepository.save(entity);
    }

    public void tables(List<DwsTable> list) {
        dwsTableRepository.saveAll(list);
    }

    /**
     * DwsColumn
     */
    public void column(DwsColumn entity) {
        dwsColumnRepository.save(entity);
    }

    public void columns(List<DwsColumn> list) {
        dwsColumnRepository.saveAll(list);
    }
}