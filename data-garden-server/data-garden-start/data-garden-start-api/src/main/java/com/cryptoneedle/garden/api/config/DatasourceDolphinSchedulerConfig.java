package com.cryptoneedle.garden.api.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * <p>description: 配置 DolphinScheduler 数据源 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Configuration
@ConditionalOnBooleanProperty(name = "dolphin-scheduler.enabled")
public class DatasourceDolphinSchedulerConfig {
    
    @Bean(name = "dolphinSchedulerDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.dolphin-scheduler")
    public DataSource dolphinSchedulerDataSource() {
        return new DruidDataSource();
    }
    
    @Bean(name = "dolphinSchedulerJdbcTemplate")
    public JdbcTemplate dolphinSchedulerJdbcTemplate(@Qualifier("dolphinSchedulerDataSource") DataSource dolphinSchedulerDataSource) {
        return new JdbcTemplate(dolphinSchedulerDataSource);
    }
}