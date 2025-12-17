package com.cryptoneedle.garden.core.source;

import com.cryptoneedle.garden.common.vo.source.SourceCatalogAddVo;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.spi.DataSourceManager;
import com.cryptoneedle.garden.spi.DataSourceProvider;
import com.cryptoneedle.garden.spi.DataSourceSpiLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>description: 配置-数据源目录-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-16
 */
@Slf4j
@Service
public class SourceCatalogService {
    
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public SourceCatalogService(AddService addService,
                                SelectService selectService,
                                SaveService saveService,
                                DeleteService deleteService,
                                PatchService patchService) {
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
    }
    
    public void add(SourceCatalogAddVo vo) {
        SourceCatalog catalog = add.source.catalog(vo);
        
        // testServer
        // testJdbc
        // testDoris
    }
    
    private boolean testConnection(SourceCatalog catalog) {
        try {
            DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
            if (provider != null) {
                String url = provider.buildJdbcUrl(catalog);
                catalog.setUrl(url);
            }
            return DataSourceManager.testConnection(catalog);
        } catch (Exception e) {
            log.warn("Test connection failed", e);
            return false;
        }
    }
}