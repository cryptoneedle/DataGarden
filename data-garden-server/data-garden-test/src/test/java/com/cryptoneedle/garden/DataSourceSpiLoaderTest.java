package com.cryptoneedle.garden;

import com.cryptoneedle.garden.spi.DataSourceSpiLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * <p>description: 数据源SPI加载器测试 </p>
 *
 * @author CryptoNeedle
 * @date 2025-12-01
 */
@Slf4j
class DataSourceSpiLoaderTest {
    
    @Test
    @DisplayName("测试数据源插件注册与重载")
    void test() {
        DataSourceSpiLoader.initialize();
        DataSourceSpiLoader.reload();
    }
}