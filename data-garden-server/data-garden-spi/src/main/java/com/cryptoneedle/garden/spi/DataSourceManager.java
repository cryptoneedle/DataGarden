package com.cryptoneedle.garden.spi;

import com.alibaba.druid.pool.DruidDataSource;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * <p>description: 数据源-管理器 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-01
 */
@Slf4j
public class DataSourceManager {
    
    private static final Map<String, PooledDataSourceWrapper> DATA_SOURCE_MAP = new ConcurrentHashMap<>();
    private static final Lock GLOBAL_LOCK = new ReentrantLock();
    
    public static Connection getConnection(SourceCatalog catalog) throws SQLException {
        String catalogName = catalog.getId().getCatalogName();
        
        PooledDataSourceWrapper existing = DATA_SOURCE_MAP.get(catalogName);
        if (existing != null && existing.getSourceCatalog().equalsJdbc(catalog)) {
            return existing.dataSource.getConnection();
        }
        
        GLOBAL_LOCK.lock();
        try {
            existing = DATA_SOURCE_MAP.get(catalogName);
            if (existing != null && existing.getSourceCatalog().equalsJdbc(catalog)) {
                return existing.dataSource.getConnection();
            }
            // 关闭旧数据源
            log.info("[JDBC] 检测到配置变更，关闭旧数据源: {}", catalogName);
            if (existing != null && existing.dataSource != null && !existing.dataSource.isClosed()) {
                existing.dataSource.close();
                log.info("[JDBC] 关闭连接 -> {}", existing.catalogName);
            }
            // 创建新数据源
            PooledDataSourceWrapper newWrapper = createPooledDataSource(catalog);
            DATA_SOURCE_MAP.put(catalogName, newWrapper);
            return newWrapper.dataSource.getConnection();
        } finally {
            GLOBAL_LOCK.unlock();
        }
    }
    
    public static JdbcTemplate getJdbcTemplate(SourceCatalog catalog) {
        String catalogName = catalog.getId().getCatalogName();
        
        PooledDataSourceWrapper existing = DATA_SOURCE_MAP.get(catalogName);
        if (existing != null && existing.getSourceCatalog().equalsJdbc(catalog)) {
            return existing.jdbcTemplate;
        }
        
        GLOBAL_LOCK.lock();
        try {
            existing = DATA_SOURCE_MAP.get(catalogName);
            if (existing != null && existing.getSourceCatalog().equalsJdbc(catalog)) {
                return existing.jdbcTemplate;
            }
            // 关闭旧数据源
            log.info("[JDBC] 检测到配置变更，关闭旧数据源: {}", catalogName);
            if (existing != null && existing.dataSource != null && !existing.dataSource.isClosed()) {
                existing.dataSource.close();
                log.info("[JDBC] 关闭连接 -> {}", existing.catalogName);
            }
            // 创建新数据源
            PooledDataSourceWrapper newWrapper = createPooledDataSource(catalog);
            DATA_SOURCE_MAP.put(catalogName, newWrapper);
            return newWrapper.jdbcTemplate;
        } finally {
            GLOBAL_LOCK.unlock();
        }
    }
    
    public static boolean testConnection(SourceCatalog catalog) {
        String catalogName = catalog.getId().getCatalogName();
        log.info("[JDBC] 开始测试连接 -> {}", catalogName);
        DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
        if (provider == null) {
            log.warn("[JDBC] 测试连接失败 -> 未找到 {} 对应的实现插件", catalog.getDatabaseType());
            return false;
        }
        String jdbcUrl = provider.buildJdbcUrl(catalog);
        
        // 只开启一次连接
        try (Connection connection = DriverManager.getConnection(jdbcUrl, catalog.getUsername(), catalog.getPassword())) {
            // todo jdts无法执行isValid做出了一些妥协
            // 1. 如果 isValid 返回 false (极少见)，尝试执行简单查询作为保底
            try (Statement stmt = connection.createStatement()) {
                stmt.setQueryTimeout(5);
                String validationSql = "SELECT 1";
                stmt.execute(validationSql);
                return true;
            } catch (Exception ex) {
            
            }
            
            // 2. 优先使用 JDBC 4 标准的 isValid 方法
            connection.isValid(5);
            log.info("[JDBC] 连接测试成功 (isValid) -> {}", catalogName);
            return true;
            
            
        } catch (SQLException e) {
            log.error("[JDBC] 连接测试异常 -> 目录: {}, URL: {}, 错误: {}",
                    catalogName, jdbcUrl, e.getMessage(), e);
            // 抛出自定义异常或直接返回 false，取决于业务需求
            throw new RuntimeException("数据库连接失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("[JDBC] 发生非预期错误 -> {}", catalogName, e);
            return false;
        }
    }
    
    public static Map<String, Boolean> healthCheck() {
        return DATA_SOURCE_MAP.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
            try (Connection conn = e.getValue().dataSource.getConnection()) {
                return conn.isValid(5);
            } catch (Exception ex) {
                return false;
            }
        }));
    }
    
    public static void closeDataSource(String catalogName) {
        PooledDataSourceWrapper wrapper = DATA_SOURCE_MAP.remove(catalogName);
        if (wrapper != null) {
            wrapper.close();
        }
    }
    
    public static void closeAllDataSources() {
        // 复制一份防止死锁
        Map<String, PooledDataSourceWrapper> copy = new HashMap<>(DATA_SOURCE_MAP);
        DATA_SOURCE_MAP.clear();
        copy.values().parallelStream().forEach(PooledDataSourceWrapper::close);
        log.info("[JDBC] 已关闭所有数据源连接");
    }
    
    private static PooledDataSourceWrapper createPooledDataSource(SourceCatalog catalog) {
        String catalogName = catalog.getId().getCatalogName();
        DataSourceProvider provider = DataSourceSpiLoader.getProvider(catalog.getDatabaseType());
        
        DruidDataSource dataSource = null;
        try {
            dataSource = new DruidDataSource();
            dataSource.setName(catalogName);
            dataSource.setUrl(provider.buildJdbcUrl(catalog));
            dataSource.setUsername(catalog.getUsername());
            dataSource.setPassword(catalog.getPassword());
            // 连接池配置
            dataSource.setInitialSize(1);                                // 初始连接数
            dataSource.setMinIdle(1);                                    // 最小空闲连接
            dataSource.setMaxActive(3);                                  // 最大活跃连接
            dataSource.setMaxWait(10000);                                // 获取连接最大等待时间（10秒）
            dataSource.setTimeBetweenEvictionRunsMillis(60000);          // 检测间隔（1分钟）
            dataSource.setMinEvictableIdleTimeMillis(600000);            // 空闲连接最小生存时间（10分钟）
            dataSource.setValidationQueryTimeout(5);                     // 设置验证查询超时时间
            dataSource.setTestWhileIdle(true);                           // 空闲时测试连接
            dataSource.setTestOnBorrow(false);                           // 获取时不测试
            dataSource.setTestOnReturn(false);                           // 归还时不测试
            dataSource.setPoolPreparedStatements(true);                  // 开启PSCache
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(20); // 预处理语句缓存最大数量
            dataSource.setKeepAlive(true);                               // 保持连接
            
            dataSource.init();
            
            // 验证连接
            if (!testConnection(catalog)) {
                throw new SQLException("[JDBC] 连接验证失败");
            }
            
            log.info("[JDBC] 创建连接成功 -> {}, activeCount={}, poolingCount={}",
                    catalogName, dataSource.getActiveCount(), dataSource.getPoolingCount());
            
            return new PooledDataSourceWrapper(catalog, dataSource);
        } catch (Exception e) {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (Exception closeEx) {
                    log.error("[JDBC] 关闭失败的数据源时出错: {}", catalogName, closeEx);
                }
            }
            throw new RuntimeException("[JDBC] 创建连接失败 -> " + catalogName, e);
        }
    }
    
    /**
     * 数据源-包装器
     */
    @Getter
    private static class PooledDataSourceWrapper {
        private final String catalogName;
        private final SourceCatalog sourceCatalog;
        private final DruidDataSource dataSource;
        private final JdbcTemplate jdbcTemplate;
        
        public PooledDataSourceWrapper(SourceCatalog catalog, DruidDataSource dataSource) {
            this.catalogName = catalog.getId().getCatalogName();
            this.sourceCatalog = catalog;
            this.dataSource = dataSource;
            this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        }
        
        public void close() {
            if (dataSource != null && !dataSource.isClosed()) {
                try {
                    dataSource.close();
                    log.info("[JDBC] 关闭连接 -> {}", catalogName);
                } catch (Exception e) {
                    log.error("[JDBC] 关闭连接 -> {}", catalogName, e);
                }
            }
        }
    }
}