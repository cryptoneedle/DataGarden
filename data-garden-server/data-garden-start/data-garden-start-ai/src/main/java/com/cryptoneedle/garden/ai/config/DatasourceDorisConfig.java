package com.cryptoneedle.garden.ai.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * <p>description: 配置 Doris 数据源 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Configuration
@EnableTransactionManagement
@ConditionalOnBooleanProperty(name = "doris.enabled")
public class DatasourceDorisConfig {

    @Bean(name = "dorisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.doris")
    public DataSource dorisDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "dorisJdbcTemplate")
    public JdbcTemplate dorisJdbcTemplate(@Qualifier("dorisDataSource") DataSource dorisDataSource) {
        return new JdbcTemplate(dorisDataSource);
    }
}