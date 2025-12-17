package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.common.vo.source.SourceCatalogAddVo;
import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.repository.source.*;
import com.cryptoneedle.garden.spi.DataSourceSpiLoader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 新增数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class AddSourceService {
    
    private final SelectConfigService selectConfigService;
    private final SelectSourceService selectSourceService;
    private final SaveSourceService saveSourceService;
    private final SourceCatalogRepository sourceCatalogRepository;
    private final SourceDatabaseRepository sourceDatabaseRepository;
    private final SourceTableRepository sourceTableRepository;
    private final SourceColumnRepository sourceColumnRepository;
    private final SourceDimensionRepository sourceDimensionRepository;
    
    public AddSourceService(SelectConfigService selectConfigService,
                            SelectSourceService selectSourceService,
                            SaveSourceService saveSourceService,
                            SourceCatalogRepository sourceCatalogRepository,
                            SourceDatabaseRepository sourceDatabaseRepository,
                            SourceTableRepository sourceTableRepository,
                            SourceColumnRepository sourceColumnRepository,
                            SourceDimensionRepository sourceDimensionRepository) {
        this.selectConfigService = selectConfigService;
        this.selectSourceService = selectSourceService;
        this.saveSourceService = saveSourceService;
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
        saveSourceService.catalog(entity);
    }
    
    public void catalogs(List<SourceCatalog> list) {
        saveSourceService.catalogs(list);
    }
    
    public SourceCatalog catalog(SourceCatalogAddVo vo) {
        SourceCatalogKey key = SourceCatalogKey.builder().catalogName(vo.getCatalogName()).build();
        SourceCatalog entity = selectSourceService.catalog(key);
        if (entity != null) {
            throw new RuntimeException("数据源目录: " + key + "已存在");
        }
        
        entity = SourceCatalog.builder()
                              .id(key)
                              .dorisCatalogName(vo.getDorisCatalogName())
                              .systemCode(vo.getSystemCode())
                              .collectFrequency(vo.getCollectFrequency())
                              .collectTimePoint(vo.getCollectTimePoint())
                              .host(vo.getHost())
                              .port(vo.getPort())
                              .databaseType(vo.getDatabaseType())
                              .connectType(vo.getConnectType())
                              .route(vo.getRoute())
                              .username(vo.getUsername())
                              .password(vo.getPassword())
                              // 初始化
                              .url(null)
                              .version(null)
                              .serverConnected(false)
                              .jdbcConnected(false)
                              .dorisConnected(false)
                              .serverConnectedDt(null)
                              .jdbcConnectedDt(null)
                              .dorisConnectedDt(null)
                              .enabled(false)
                              .configSsh(null)
                              .build();
        
        entity.setUrl(DataSourceSpiLoader.getProvider(entity.getDatabaseType()).buildJdbcUrl(entity));
        if (StringUtils.isNotBlank(vo.getSshHost())) {
            ConfigSsh configSsh = selectConfigService.ssh(vo.getSshHost());
            entity.setConfigSsh(configSsh);
        }
        
        saveSourceService.catalog(entity);
        
        return entity;
    }
    
    /**
     * SourceDatabase
     */
    public void database(SourceDatabase entity) {
        saveSourceService.database(entity);
    }
    
    public void databases(List<SourceDatabase> list) {
        saveSourceService.databases(list);
    }
    
    /**
     * SourceTable
     */
    public void table(SourceTable entity) {
        saveSourceService.table(entity);
    }
    
    public void tables(List<SourceTable> list) {
        saveSourceService.tables(list);
    }
    
    /**
     * SourceColumn
     */
    public void column(SourceColumn entity) {
        saveSourceService.column(entity);
    }
    
    public void columns(List<SourceColumn> list) {
        saveSourceService.columns(list);
    }
    
    /**
     * SourceDimension
     */
    public void dimension(SourceDimension entity) {
        saveSourceService.dimension(entity);
    }
    
    public void dimensions(List<SourceDimension> list) {
        saveSourceService.dimensions(list);
    }
}