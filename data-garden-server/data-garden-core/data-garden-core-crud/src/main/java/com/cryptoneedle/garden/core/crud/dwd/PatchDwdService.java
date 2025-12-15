package com.cryptoneedle.garden.core.crud.dwd;


import com.cryptoneedle.garden.infrastructure.repository.dwd.DwdColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dwd.DwdTableRepository;
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
public class PatchDwdService {
    
    private final DwdTableRepository dwdTableRepository;
    private final DwdColumnRepository dwdColumnRepository;
    
    public PatchDwdService(DwdTableRepository dwdTableRepository,
                           DwdColumnRepository dwdColumnRepository) {
        this.dwdTableRepository = dwdTableRepository;
        this.dwdColumnRepository = dwdColumnRepository;
    }
    
    /**
     * DwdTable
     */
    
    /**
     * DwdColumn
     */
}