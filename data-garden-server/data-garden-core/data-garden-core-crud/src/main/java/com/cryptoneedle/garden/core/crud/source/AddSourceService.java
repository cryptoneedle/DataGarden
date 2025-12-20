package com.cryptoneedle.garden.core.crud.source;

import com.cryptoneedle.garden.common.key.source.SourceCatalogKey;
import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceCatalogSaveVo;
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
    private final SelectSourceService select;
    private final SaveSourceService save;
    
    public AddSourceService(SelectConfigService selectConfigService,
                            SelectSourceService selectSourceService,
                            SaveSourceService saveSourceService) {
        this.selectConfigService = selectConfigService;
        this.select = selectSourceService;
        this.save = saveSourceService;
    }
    
    /**
     * SourceCatalog
     */
    public void catalog(SourceCatalog entity) {
        save.catalog(entity);
    }
    
    public SourceCatalog catalog(SourceCatalogSaveVo vo) {
        SourceCatalogKey key = vo.sourceCatalogKey();
        SourceCatalog entity = select.catalog(key);
        if (entity != null) {
            throw new RuntimeException("数据源目录: " + key + "已存在");
        }
        
        entity = vo.sourceCatalog();
        entity.setUrl(DataSourceSpiLoader.getProvider(entity.getDatabaseType()).buildJdbcUrl(entity));
        if (StringUtils.isNotBlank(vo.getSshHost())) {
            ConfigSsh configSsh = selectConfigService.ssh(vo.getSshHost());
            entity.setConfigSsh(configSsh);
        }
        
        save.catalog(entity);
        
        return entity;
    }
    
    public void catalogs(List<SourceCatalog> list) {
        save.catalogs(list);
    }
    
    /**
     * SourceDatabase
     */
    public void database(SourceDatabase entity) {
        save.database(entity);
    }
    
    public void databases(List<SourceDatabase> list) {
        save.databases(list);
    }
    
    /**
     * SourceTable
     */
    public void table(SourceTable entity) {
        save.table(entity);
    }
    
    public void tables(List<SourceTable> list) {
        save.tables(list);
    }
    
    /**
     * SourceColumn
     */
    public void column(SourceColumn entity) {
        save.column(entity);
    }
    
    public void columns(List<SourceColumn> list) {
        save.columns(list);
    }
    
    /**
     * SourceDimension
     */
    public void dimension(SourceDimension entity) {
        save.dimension(entity);
    }
    
    public void dimensions(List<SourceDimension> list) {
        save.dimensions(list);
    }
}