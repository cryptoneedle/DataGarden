package com.cryptoneedle.garden.core.crud.dws;


import com.cryptoneedle.garden.infrastructure.repository.dws.DwsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dws.DwsTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>description: 部分更新数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class PatchDwsService {

    private final DwsTableRepository dwsTableRepository;
    private final DwsColumnRepository dwsColumnRepository;

    public PatchDwsService(DwsTableRepository dwsTableRepository,
                           DwsColumnRepository dwsColumnRepository) {
        this.dwsTableRepository = dwsTableRepository;
        this.dwsColumnRepository = dwsColumnRepository;
    }

    /**
     * DwsTable
     */

    /**
     * DwsColumn
     */
}