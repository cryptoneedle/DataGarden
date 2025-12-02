package com.cryptoneedle.garden.spi.datasource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>description: 数据源-SPI加载器 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-29
 */
@Slf4j
public class DataSourceSpiLoader {

    private static final Map<String, DataSourceProvider> PROVIDERS = new ConcurrentHashMap<>();

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    public static void initialize() {
        if (INITIALIZED.get()) {
            return;
        }

        synchronized (DataSourceSpiLoader.class) {
            if (!INITIALIZED.get()) {
                loadProviders();
                INITIALIZED.set(true);
            }
        }
    }

    private static void loadProviders() {
        int loadedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;

        ServiceLoader<DataSourceProvider> serviceLoader = ServiceLoader.load(DataSourceProvider.class);
        for (DataSourceProvider provider : serviceLoader) {
            try {
                if (registerProvider(provider)) {
                    loadedCount++;
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                failedCount++;
                log.error("[SPI] 注册插件 -> {}", provider.getClass().getName(), e);
            }
        }

        if (loadedCount == 0) {
            log.error("[SPI] 注册插件 -> 未加载到任何数据源插件");
        } else {
            log.info("[SPI] 注册插件 -> 成功 {} | 跳过 {} | 失败 {}", loadedCount, skippedCount, failedCount);
        }
    }

    private static boolean registerProvider(DataSourceProvider provider) {
        String databaseType = provider.getDatabaseType();
        if (StringUtils.isBlank(databaseType)) {
            log.warn("[SPI] 验证插件 -> {} getDatabaseType() 为空", provider.getClass().getName());
            return false;
        }

        String normalizedType = normalizeDatabaseType(databaseType);
        DataSourceProvider existing = PROVIDERS.putIfAbsent(normalizedType, provider);
        if (existing != null) {
            log.warn("[SPI] 验证插件 -> [{}] 存在多个实现，保留: {}，忽略: {}", normalizedType, existing.getClass().getName(), provider.getClass().getName());
            return false;
        }

        log.info("[SPI] 注册插件 -> {}", normalizedType);
        return true;
    }

    private static String normalizeDatabaseType(String databaseType) {
        // 标准化数据库类型
        return StringUtils.trimToEmpty(databaseType).toUpperCase(Locale.ROOT);
    }

    public static DataSourceProvider getProvider(String databaseType) {
        if (StringUtils.isBlank(databaseType)) {
            throw new IllegalArgumentException("[SPI] databaseType 不能为空");
        }

        initialize();

        String normalized = normalizeDatabaseType(databaseType);
        DataSourceProvider provider = PROVIDERS.get(normalized);

        if (provider == null) {
            throw new IllegalArgumentException("[SPI] 未找到 ["+ databaseType +"] 对应的实现");
        }

        return provider;
    }

    public static Set<String> getSupportedDatabaseTypes() {
        initialize();
        return Collections.unmodifiableSet(new HashSet<>(PROVIDERS.keySet()));
    }

    public static boolean isSupported(String databaseType) {
        if (StringUtils.isBlank(databaseType)) {
            log.warn("[SPI] 验证插件 -> databaseType 为空");
            return false;
        }
        initialize();
        return PROVIDERS.containsKey(normalizeDatabaseType(databaseType));
    }

    public static void reload() {
        log.info("[SPI] 开始重载插件");

        INITIALIZED.set(false);
        PROVIDERS.clear();

        initialize();
    }
}
