package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.repository.source.*;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceCatalogSaveVo;
import com.cryptoneedle.garden.spi.DataSourceSpiLoader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 保存数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SaveSourceService {
    
    private final SelectConfigService selectConfigService;
    private final SelectSourceService select;
    private final SaveSourceService save;
    private final SourceCatalogRepository sourceCatalogRepository;
    private final SourceDatabaseRepository sourceDatabaseRepository;
    private final SourceTableRepository sourceTableRepository;
    private final SourceColumnRepository sourceColumnRepository;
    private final SourceDimensionRepository sourceDimensionRepository;
    
    public SaveSourceService(SelectConfigService selectConfigService,
                             SelectSourceService selectSourceService,
                             @Lazy SaveSourceService saveSourceService,
                             SourceCatalogRepository sourceCatalogRepository,
                             SourceDatabaseRepository sourceDatabaseRepository,
                             SourceTableRepository sourceTableRepository,
                             SourceColumnRepository sourceColumnRepository,
                             SourceDimensionRepository sourceDimensionRepository) {
        this.selectConfigService = selectConfigService;
        this.select = selectSourceService;
        this.save = saveSourceService;
        this.sourceCatalogRepository = sourceCatalogRepository;
        this.sourceDatabaseRepository = sourceDatabaseRepository;
        this.sourceTableRepository = sourceTableRepository;
        this.sourceColumnRepository = sourceColumnRepository;
        this.sourceDimensionRepository = sourceDimensionRepository;
    }
    
    /**
     * SourceCatalog
     */
    public void catalog(SourceCatalog entity) {
        sourceCatalogRepository.save(entity);
    }
    
    public SourceCatalog catalog(SourceCatalogSaveVo vo) {
        SourceCatalogKey key = vo.sourceCatalogKey();
        SourceCatalog old = select.catalog(key);
        
        SourceCatalog entity = vo.sourceCatalog(old);
        entity.setUrl(DataSourceSpiLoader.getProvider(entity.getDatabaseType()).buildJdbcUrl(entity));
        if (StringUtils.isNotBlank(vo.getSshHost())) {
            ConfigSsh configSsh = selectConfigService.ssh(vo.getSshHost());
            entity.setSshHost(vo.getSshHost());
            entity.setConfigSsh(configSsh);
        } else {
            entity.setSshHost(null);
            entity.setConfigSsh(null);
        }
        entity.setDorisCatalog(StringUtils.lowerCase(entity.getDorisCatalog()));
        entity.setSystemCode(StringUtils.lowerCase(entity.getSystemCode()));
        
        save.catalog(entity);
        
        return entity;
    }
    
    public void catalogs(List<SourceCatalog> list) {
        sourceCatalogRepository.saveAll(list);
    }
    
    /**
     * SourceDatabase
     */
    public void database(SourceDatabase entity) {
        sourceDatabaseRepository.save(entity);
    }
    
    public void databases(List<SourceDatabase> list) {
        sourceDatabaseRepository.saveAll(list);
    }
    
    /**
     * SourceTable
     */
    public void table(SourceTable entity) {
        sourceTableRepository.save(entity);
    }
    
    public void tables(List<SourceTable> list) {
        sourceTableRepository.saveAll(list);
    }
    
    /**
     * SourceColumn
     */
    public void column(SourceColumn entity) {
        sourceColumnRepository.save(entity);
    }
    
    public void columns(List<SourceColumn> list) {
        sourceColumnRepository.saveAll(list);
    }
    
    /**
     * SourceDimension
     */
    public void dimension(SourceDimension entity) {
        sourceDimensionRepository.save(entity);
    }
    
    public void dimensions(List<SourceDimension> list) {
        sourceDimensionRepository.saveAll(list);
    }
}