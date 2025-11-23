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
 * <p>description: 新增Doris数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class AddDorisService {

    private final SaveDorisService saveDorisService;
    private final DorisCatalogRepository dorisCatalogRepository;
    private final DorisDatabaseRepository dorisDatabaseRepository;
    private final DorisTableRepository dorisTableRepository;
    private final DorisColumnRepository dorisColumnRepository;

    public AddDorisService(SaveDorisService saveDorisService,
                           DorisCatalogRepository dorisCatalogRepository,
                           DorisDatabaseRepository dorisDatabaseRepository,
                           DorisTableRepository dorisTableRepository,
                           DorisColumnRepository dorisColumnRepository) {
        this.saveDorisService = saveDorisService;
        this.dorisCatalogRepository = dorisCatalogRepository;
        this.dorisDatabaseRepository = dorisDatabaseRepository;
        this.dorisTableRepository = dorisTableRepository;
        this.dorisColumnRepository = dorisColumnRepository;
    }

    /**
     * DorisCatalog
     */
    public void catalog(DorisCatalog entity) {
        saveDorisService.catalog(entity);
    }

    public void catalogs(List<DorisCatalog> list) {
        saveDorisService.catalogs(list);
    }

    /**
     * DorisDatabase
     */
    public void database(DorisDatabase entity) {
        saveDorisService.database(entity);
    }

    public void databases(List<DorisDatabase> list) {
        saveDorisService.databases(list);
    }

    /**
     * DorisTable
     */
    public void table(DorisTable entity) {
        saveDorisService.table(entity);
    }

    public void tables(List<DorisTable> list) {
        saveDorisService.tables(list);
    }

    /**
     * DorisColumn
     */
    public void column(DorisColumn entity) {
        saveDorisService.column(entity);
    }

    public void columns(List<DorisColumn> list) {
        saveDorisService.columns(list);
    }
}