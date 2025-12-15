package com.cryptoneedle.garden.api.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>description: 配置主数据源 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {"com.cryptoneedle.garden.infrastructure.entity"})
@EnableJpaRepositories(basePackages = {"com.cryptoneedle.garden.infrastructure.repository"},
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager")
public class DatasourcePrimaryConfig {
    
    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return new DruidDataSource();
    }
    
    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource primaryDataSource, Environment environment) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.default_schema", environment.getProperty("spring.datasource.primary.default_schema", "public"));
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.datasource.primary.ddl-auto", "none"));
        properties.put("hibernate.show_sql", environment.getProperty("spring.datasource.primary.show-sql", "false"));
        
        return builder
                .dataSource(primaryDataSource)
                .packages(
                        "com.cryptoneedle.garden.infrastructure.entity"
                )
                .persistenceUnit("primary")
                .properties(properties)
                .build();
    }
    
    @Bean(name = "primaryTransactionManager")
    @Primary
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory.getObject());
    }
    
    @Bean(name = "primaryJdbcTemplate")
    @Primary
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }
}