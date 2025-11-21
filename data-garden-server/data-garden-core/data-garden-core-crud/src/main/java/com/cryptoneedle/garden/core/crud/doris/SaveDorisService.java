package com.cryptoneedle.garden.core.crud.doris;

import com.cryptoneedle.garden.infrastructure.repository.doris.DorisCatalogRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisDatabaseRepository;
import com.cryptoneedle.garden.infrastructure.repository.doris.DorisTableRepository;
import org.springframework.stereotype.Service;

/**
 * <p>description: 保存Doris数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class SaveDorisService {

    private final DorisCatalogRepository dorisCatalogRepository;
    private final DorisDatabaseRepository dorisDatabaseRepository;
    private final DorisTableRepository dorisTableRepository;
    private final DorisColumnRepository dorisColumnRepository;

    public SaveDorisService(DorisCatalogRepository dorisCatalogRepository,
                            DorisDatabaseRepository dorisDatabaseRepository,
                            DorisTableRepository dorisTableRepository,
                            DorisColumnRepository dorisColumnRepository) {
        this.dorisCatalogRepository = dorisCatalogRepository;
        this.dorisDatabaseRepository = dorisDatabaseRepository;
        this.dorisTableRepository = dorisTableRepository;
        this.dorisColumnRepository = dorisColumnRepository;
    }
}