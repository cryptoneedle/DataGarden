package com.cryptoneedle.garden.core.source;

import cn.hutool.v7.socket.SocketUtil;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.vo.source.SourceCatalogSaveVo;
import com.cryptoneedle.garden.spi.DataSourceManager;
import com.cryptoneedle.garden.spi.DataSourceProvider;
import com.cryptoneedle.garden.spi.DataSourceSpiLoader;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.Socket;
import java.time.LocalDateTime;

/**
 * <p>description: 配置-数据源目录-服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-16
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SourceCatalogService {
    
    public final SourceCatalogService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public SourceCatalogService(@Lazy SourceCatalogService sourceCatalogService,
                                AddService addService,
                                SelectService selectService,
                                SaveService saveService,
                                DeleteService deleteService,
                                PatchService patchService) {
        this.service = sourceCatalogService;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
    }
    
    public boolean testServer(SourceCatalog catalog, boolean needStore) {
        boolean connected = false;
        try (Socket socket = SocketUtil.connect(catalog.getHost(), catalog.getPort())) {
            connected = socket.isConnected();
            
            // 持久化
            if (needStore) {
                select.source.catalogCheck(catalog.getId());
                catalog.setServerConnected(connected);
                if (connected) {
                    catalog.setServerConnectedDt(LocalDateTime.now());
                }
                save.source.catalog(catalog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connected;
    }
    
    public boolean testJdbc(SourceCatalog catalog, boolean needStore) {
        boolean connected = false;
        try {
            DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
            if (provider != null) {
                String url = provider.buildJdbcUrl(catalog);
                catalog.setUrl(url);
            }
            connected = DataSourceManager.testConnection(catalog);
            // 持久化
            if (needStore) {
                select.source.catalogCheck(catalog.getId());
                catalog.setJdbcConnected(connected);
                if (connected) {
                    catalog.setJdbcConnectedDt(LocalDateTime.now());
                }
                save.source.catalog(catalog);
            }
        } catch (Exception e) {
            log.warn("Test connection failed", e);
            return false;
        }
        return connected;
    }
    
    public boolean testDoris(SourceCatalog catalog, boolean needStore) {
        return true;
    }
    
    public void addVo(SourceCatalogSaveVo vo) {
        SourceCatalog catalog = add.source.catalog(vo);
        // 测试
        service.testServer(catalog, true);
        service.testJdbc(catalog, true);
        service.testDoris(catalog, true);
    }
    
    public void saveVo(@Valid SourceCatalogSaveVo vo) {
        SourceCatalog catalog = save.source.catalog(vo);
        // 测试
        service.testServer(catalog, true);
        service.testJdbc(catalog, true);
        service.testDoris(catalog, true);
    }
}