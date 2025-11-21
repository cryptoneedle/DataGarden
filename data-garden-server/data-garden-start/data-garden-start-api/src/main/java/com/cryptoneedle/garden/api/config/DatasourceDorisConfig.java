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
 * <p>description: 配置 Doris 数据源 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Configuration
@EnableTransactionManagement
public class DatasourceDorisConfig {

    @Bean(name = "dorisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.doris")
    public DataSource dorisDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "dorisEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dorisEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dorisDataSource") DataSource dorisDataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.show_sql", false);

        return builder
                .dataSource(dorisDataSource)
                .packages(
                        "com.cryptoneedle.garden.infrastructure.entity"
                )
                .persistenceUnit("doris")
                .properties(properties)
                .build();
    }

    @Bean(name = "dorisTransactionManager")
    public PlatformTransactionManager dorisTransactionManager(
            @Qualifier("dorisEntityManagerFactory") LocalContainerEntityManagerFactoryBean dorisEntityManagerFactory) {
        return new JpaTransactionManager(dorisEntityManagerFactory.getObject());
    }

    @Bean(name = "dorisJdbcTemplate")
    public JdbcTemplate dorisJdbcTemplate(@Qualifier("dorisDataSource") DataSource dorisDataSource) {
        return new JdbcTemplate(dorisDataSource);
    }
}