package com.cryptoneedle.garden.core.crud.ods;


import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumnTranslate;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsColumnTranslateRepository;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsTableRepository;
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
public class SaveOdsService {
    
    private final OdsTableRepository odsTableRepository;
    private final OdsColumnRepository odsColumnRepository;
    private final OdsColumnTranslateRepository odsColumnTranslateRepository;
    
    public SaveOdsService(OdsTableRepository odsTableRepository,
                          OdsColumnRepository odsColumnRepository,
                          OdsColumnTranslateRepository odsColumnTranslateRepository) {
        this.odsTableRepository = odsTableRepository;
        this.odsColumnRepository = odsColumnRepository;
        this.odsColumnTranslateRepository = odsColumnTranslateRepository;
    }
    
    /**
     * OdsTable
     */
    public void table(OdsTable entity) {
        odsTableRepository.save(entity);
    }
    
    public void tables(List<OdsTable> list) {
        odsTableRepository.saveAll(list);
    }
    
    /**
     * OdsColumn
     */
    public void column(OdsColumn entity) {
        odsColumnRepository.save(entity);
    }
    
    public void columns(List<OdsColumn> list) {
        odsColumnRepository.saveAll(list);
    }
    
    public void columnTranslates(List<OdsColumnTranslate> list) {
        odsColumnTranslateRepository.saveAll(list);
    }
}