package com.cryptoneedle.garden.core.crud.standard;


import com.cryptoneedle.garden.infrastructure.entity.standard.StandardColumn;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardTable;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardTableRepository;
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
public class AddStandardService {

    private final SaveStandardService saveStandardService;
    private final StandardTableRepository standardTableRepository;
    private final StandardColumnRepository standardColumnRepository;

    public AddStandardService(SaveStandardService saveStandardService,
                              StandardTableRepository standardTableRepository,
                              StandardColumnRepository standardColumnRepository) {
        this.saveStandardService = saveStandardService;
        this.standardTableRepository = standardTableRepository;
        this.standardColumnRepository = standardColumnRepository;
    }

    /**
     * StandardTable
     */
    public void table(StandardTable entity) {
        saveStandardService.table(entity);
    }

    public void tables(List<StandardTable> list) {
        saveStandardService.tables(list);
    }

    /**
     * StandardColumn
     */
    public void column(StandardColumn entity) {
        saveStandardService.column(entity);
    }

    public void columns(List<StandardColumn> list) {
        saveStandardService.columns(list);
    }
}