package com.cryptoneedle.garden.core.crud.ods;


import com.cryptoneedle.garden.infrastructure.repository.ods.OdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsTableRepository;
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
public class PatchOdsService {

    private final OdsTableRepository odsTableRepository;
    private final OdsColumnRepository odsColumnRepository;

    public PatchOdsService(OdsTableRepository odsTableRepository,
                           OdsColumnRepository odsColumnRepository) {
        this.odsTableRepository = odsTableRepository;
        this.odsColumnRepository = odsColumnRepository;
    }

    /**
     * OdsTable
     */

    /**
     * OdsColumn
     */
}