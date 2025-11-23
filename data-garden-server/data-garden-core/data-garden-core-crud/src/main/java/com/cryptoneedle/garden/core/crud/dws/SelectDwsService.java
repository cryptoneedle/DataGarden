package com.cryptoneedle.garden.core.crud.dws;


import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsTable;
import com.cryptoneedle.garden.infrastructure.repository.ads.AdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ads.AdsTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 查询数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true)
public class SelectDwsService {

    private final AdsTableRepository adsTableRepository;
    private final AdsColumnRepository adsColumnRepository;

    public SelectDwsService(AdsTableRepository adsTableRepository,
                            AdsColumnRepository adsColumnRepository) {
        this.adsTableRepository = adsTableRepository;
        this.adsColumnRepository = adsColumnRepository;
    }

    /**
     * AdsTable
     */
    public AdsTable table(DorisTableKey id) {
        return adsTableRepository.findById(id).orElse(null);
    }

    public AdsTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return adsTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("AdsTable", id.toString()));
    }

    public List<AdsTable> tables() {
        return adsTableRepository.tables();
    }

    public List<AdsTable> tables(String databaseName) {
        return adsTableRepository.tables(databaseName);
    }

    /**
     * AdsColumn
     */
    public AdsColumn column(DorisColumnKey id) {
        return adsColumnRepository.findById(id).orElse(null);
    }

    public AdsColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return adsColumnRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("AdsColumn", id.toString()));
    }

    public List<AdsColumn> columns() {
        return adsColumnRepository.columns();
    }

    public List<AdsColumn> columns(String tableName) {
        return adsColumnRepository.columns(tableName);
    }
}