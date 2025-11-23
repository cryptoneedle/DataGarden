package com.cryptoneedle.garden.core.crud.doris;

import com.cryptoneedle.garden.infrastructure.entity.doris.DorisCatalog;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisDatabase;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisCatalogRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisDatabaseRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description: 删除Doris数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class DeleteDorisService {

    private final DorisCatalogRepository dorisCatalogRepository;
    private final DorisDatabaseRepository dorisDatabaseRepository;
    private final DorisTableRepository dorisTableRepository;
    private final DorisColumnRepository dorisColumnRepository;

    public DeleteDorisService(DorisCatalogRepository dorisCatalogRepository,
                              DorisDatabaseRepository dorisDatabaseRepository,
                              DorisTableRepository dorisTableRepository,
                              DorisColumnRepository dorisColumnRepository) {
        this.dorisCatalogRepository = dorisCatalogRepository;
        this.dorisDatabaseRepository = dorisDatabaseRepository;
        this.dorisTableRepository = dorisTableRepository;
        this.dorisColumnRepository = dorisColumnRepository;
    }

    /**
     * DorisCatalog
     */
    public void catalog(DorisCatalog entity) {
        dorisCatalogRepository.delete(entity);
    }

    public void catalogs(List<DorisCatalog> list) {
        dorisCatalogRepository.deleteAll(list);
    }

    /**
     * DorisDatabase
     */
    public void database(DorisDatabase entity) {
        dorisDatabaseRepository.delete(entity);
    }

    public void databases(List<DorisDatabase> list) {
        dorisDatabaseRepository.deleteAll(list);
    }

    /**
     * DorisTable
     */
    public void table(DorisTable entity) {
        dorisTableRepository.delete(entity);
    }

    public void tables(List<DorisTable> list) {
        dorisTableRepository.deleteAll(list);
    }

    /**
     * DorisColumn
     */
    public void column(DorisColumn entity) {
        dorisColumnRepository.delete(entity);
    }

    public void columns(List<DorisColumn> list) {
        dorisColumnRepository.deleteAll(list);
    }
}