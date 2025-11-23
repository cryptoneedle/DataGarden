package com.cryptoneedle.garden.core.crud.standard;


import com.cryptoneedle.garden.infrastructure.repository.standard.StandardColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.standard.StandardTableRepository;
import org.springframework.stereotype.Service;

/**
 * <p>description: 部分更新数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class PatchStandardService {

    private final StandardTableRepository standardTableRepository;
    private final StandardColumnRepository standardColumnRepository;

    public PatchStandardService(StandardTableRepository standardTableRepository,
                                StandardColumnRepository standardColumnRepository) {
        this.standardTableRepository = standardTableRepository;
        this.standardColumnRepository = standardColumnRepository;
    }

    /**
     * StandardTable
     */

    /**
     * StandardColumn
     */
}