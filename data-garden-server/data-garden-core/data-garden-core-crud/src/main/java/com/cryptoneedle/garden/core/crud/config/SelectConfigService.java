package com.cryptoneedle.garden.core.crud.config;

import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.config.ConfigPropertyKey;
import com.cryptoneedle.garden.common.key.config.ConfigSshKey;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigProperty;
import com.cryptoneedle.garden.infrastructure.entity.config.ConfigSsh;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigPropertyRepository;
import com.cryptoneedle.garden.infrastructure.repository.config.ConfigSshRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>description: 查询属性配置服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SelectConfigService {
    
    private final ConfigPropertyRepository configPropertyRepository;
    private final ConfigSshRepository configSshRepository;
    
    public SelectConfigService(ConfigPropertyRepository configPropertyRepository,
                               ConfigSshRepository configSshRepository) {
        this.configPropertyRepository = configPropertyRepository;
        this.configSshRepository = configSshRepository;
    }
    
    /**
     * ConfigProperty
     */
    public ConfigProperty property(ConfigPropertyKey id) {
        return configPropertyRepository.findById(id).orElse(null);
    }
    
    public ConfigProperty property(String propertyName) {
        return property(ConfigPropertyKey.builder().propertyName(propertyName).build());
    }
    
    public ConfigProperty propertyCheck(ConfigPropertyKey id) throws EntityNotFoundException {
        return configPropertyRepository.findById(id)
                                       .orElseThrow(() -> new EntityNotFoundException("ConfigProperty", id.toString()));
    }
    
    public List<ConfigProperty> properties() {
        return configPropertyRepository.properties();
    }
    
    public String dorisSchemaOds() {
        return property("doris_schema_ods").getValue();
    }
    
    public String dorisTablePrefixOds() {
        return property("doris_table_prefix_ods").getValue();
    }
    
    public String dorisCatalogDriverUrl(String databaseType) {
        return property("doris_catalog_driver_url_" + StringUtils.lowerCase(databaseType)).getValue();
    }
    
    public String dorisCatalogDriverClass(String databaseType) {
        return property("doris_catalog_driver_class_" + StringUtils.lowerCase(databaseType)).getValue();
    }
    
    public String dorisCatalogConnectionPoolMinSize() {
        return property("doris_catalog_connection_pool_min_size").getValue();
    }
    
    public String dorisCatalogConnectionPoolMaxSize() {
        return property("doris_catalog_connection_pool_max_size").getValue();
    }
    
    public String dorisCatalogConnectionPoolMaxWaitTime() {
        return property("doris_catalog_connection_pool_max_wait_time").getValue();
    }
    
    public String dorisCatalogConnectionPoolMaxLifeTime() {
        return property("doris_catalog_connection_pool_max_life_time").getValue();
    }
    
    public String dorisCatalogConnectionPoolKeepAlive() {
        return property("doris_catalog_connection_pool_keep_alive").getValue();
    }
    
    public String dorisConfigReplicationNum() {
        return property("doris_config_replication_num").getValue();
    }
    
    public String dorisDatasourceFeHost() {
        return property("doris_datasource_fe_host").getValue();
    }
    
    public String dorisDatasourceFePort() {
        return property("doris_datasource_fe_port").getValue();
    }
    
    public String dorisDatasourceFeStreamLoadPort() {
        return property("doris_datasource_fe_stream_load_port").getValue();
    }
    
    public String dorisDatasourceUsername() {
        return property("doris_datasource_username").getValue();
    }
    
    public String dorisDatasourcePassword() {
        return property("doris_datasource_password").getValue();
    }
    
    /**
     * ConfigSsh
     */
    public ConfigSsh ssh(ConfigSshKey id) {
        return configSshRepository.findById(id).orElse(null);
    }
    
    public ConfigSsh ssh(String host) {
        return ssh(ConfigSshKey.builder().host(host).build());
    }
    
    public ConfigSsh sshCheck(ConfigSshKey id) throws EntityNotFoundException {
        return configSshRepository.findById(id)
                                  .orElseThrow(() -> new EntityNotFoundException("ConfigSsh", id.toString()));
    }
    
    public List<ConfigSsh> sshs() {
        return configSshRepository.sshs();
    }
    
    public String dolphinSchedulerHost() {
        return property("dolphin_scheduler_host").getValue();
    }
    
    public Integer dolphinSchedulerPort() {
        return Integer.valueOf(property("dolphin_scheduler_port").getValue());
    }
    
    public String dolphinSchedulerUsername() {
        return property("dolphin_scheduler_username").getValue();
    }
    
    public String dolphinSchedulerPassword() {
        return property("dolphin_scheduler_password").getValue();
    }
    
    public Long dolphinSchedulerProjectFull() {
        return Long.valueOf(property("dolphin_scheduler_project_full").getValue());
    }
    
    public String dolphinSchedulerToken() {
        return property("dolphin_scheduler_token").getValue();
    }
    
    public String dolphinSchedulerTenantCode() {
        return property("dolphin_scheduler_tenant_code").getValue();
    }
}