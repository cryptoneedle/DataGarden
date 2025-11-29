package com.cryptoneedle.garden.core.crud.dws;


import com.cryptoneedle.garden.infrastructure.entity.dws.DwsColumn;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsTable;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 删除数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DeleteDwsService {

    private final DwsTableRepository dwsTableRepository;
    private final DwsColumnRepository dwsColumnRepository;

    public DeleteDwsService(DwsTableRepository dwsTableRepository,
                            DwsColumnRepository dwsColumnRepository) {
        this.dwsTableRepository = dwsTableRepository;
        this.dwsColumnRepository = dwsColumnRepository;
    }

    /**
     * DwsTable
     */
    public void table(DwsTable entity) {
        dwsTableRepository.delete(entity);
    }

    public void tables(List<DwsTable> list) {
        dwsTableRepository.deleteAll(list);
    }

    /**
     * DwsColumn
     */
    public void column(DwsColumn entity) {
        dwsColumnRepository.delete(entity);
    }

    public void columns(List<DwsColumn> list) {
        dwsColumnRepository.deleteAll(list);
    }
}