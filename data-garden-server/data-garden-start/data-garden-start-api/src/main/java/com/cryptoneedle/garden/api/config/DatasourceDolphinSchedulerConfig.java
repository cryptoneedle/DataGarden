package com.cryptoneedle.garden.api.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>description: 配置 DolphinScheduler 数据源 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Configuration
@EnableTransactionManagement
public class DatasourceDolphinSchedulerConfig {

    @Bean(name = "dolphinSchedulerDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.dolphin-scheduler")
    public DataSource dolphinSchedulerDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "dolphinSchedulerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dolphinSchedulerEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dolphinSchedulerDataSource") DataSource dolphinSchedulerDataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.show_sql", false);

        return builder
                .dataSource(dolphinSchedulerDataSource)
                .packages(
                        "com.cryptoneedle.garden.infrastructure.entity"
                )
                .persistenceUnit("dolphinScheduler")
                .properties(properties)
                .build();
    }

    @Bean(name = "dolphinSchedulerTransactionManager")
    public PlatformTransactionManager dolphinSchedulerTransactionManager(
            @Qualifier("dolphinSchedulerEntityManagerFactory") LocalContainerEntityManagerFactoryBean dolphinSchedulerEntityManagerFactory) {
        return new JpaTransactionManager(dolphinSchedulerEntityManagerFactory.getObject());
    }

    @Bean(name = "dolphinSchedulerJdbcTemplate")
    public JdbcTemplate dolphinSchedulerJdbcTemplate(@Qualifier("dolphinSchedulerDataSource") DataSource dolphinSchedulerDataSource) {
        return new JdbcTemplate(dolphinSchedulerDataSource);
    }
}