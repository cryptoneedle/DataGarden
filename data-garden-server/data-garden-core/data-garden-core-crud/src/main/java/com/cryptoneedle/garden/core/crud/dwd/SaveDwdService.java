package com.cryptoneedle.garden.core.crud.dwd;


import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdColumn;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdTable;
import com.cryptoneedle.garden.infrastructure.repository.dwd.DwdColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.dwd.DwdTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 保存数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SaveDwdService {
    
    private final DwdTableRepository dwdTableRepository;
    private final DwdColumnRepository dwdColumnRepository;
    
    public SaveDwdService(DwdTableRepository dwdTableRepository,
                          DwdColumnRepository dwdColumnRepository) {
        this.dwdTableRepository = dwdTableRepository;
        this.dwdColumnRepository = dwdColumnRepository;
    }
    
    /**
     * DwdTable
     */
    public void table(DwdTable entity) {
        dwdTableRepository.save(entity);
    }
    
    public void tables(List<DwdTable> list) {
        dwdTableRepository.saveAll(list);
    }
    
    /**
     * DwdColumn
     */
    public void column(DwdColumn entity) {
        dwdColumnRepository.save(entity);
    }
    
    public void columns(List<DwdColumn> list) {
        dwdColumnRepository.saveAll(list);
    }
}