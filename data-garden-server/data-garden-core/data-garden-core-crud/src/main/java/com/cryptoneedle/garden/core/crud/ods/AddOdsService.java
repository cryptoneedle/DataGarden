package com.cryptoneedle.garden.core.crud.ods;


import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsTableRepository;
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
public class AddOdsService {
    
    private final SaveOdsService saveOdsService;
    private final OdsTableRepository odsTableRepository;
    private final OdsColumnRepository odsColumnRepository;
    
    public AddOdsService(SaveOdsService saveOdsService,
                         OdsTableRepository odsTableRepository,
                         OdsColumnRepository odsColumnRepository) {
        this.saveOdsService = saveOdsService;
        this.odsTableRepository = odsTableRepository;
        this.odsColumnRepository = odsColumnRepository;
    }
    
    /**
     * OdsTable
     */
    public void table(OdsTable entity) {
        saveOdsService.table(entity);
    }
    
    public void tables(List<OdsTable> list) {
        saveOdsService.tables(list);
    }
    
    /**
     * OdsColumn
     */
    public void column(OdsColumn entity) {
        saveOdsService.column(entity);
    }
    
    public void columns(List<OdsColumn> list) {
        saveOdsService.columns(list);
    }
}