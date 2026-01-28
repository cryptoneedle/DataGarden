package com.cryptoneedle.garden.api.config;

import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import org.apache.dolphinscheduler.client.DolphinSchedulerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-27
 */
@Configuration
public class DolphinSchedulerClientConfig {
    
    @Bean
    public DolphinSchedulerClient dolphinSchedulerClient(SelectConfigService selectConfigService) {
        String host = selectConfigService.dolphinSchedulerHost();
        Integer port = selectConfigService.dolphinSchedulerPort();
        String token = selectConfigService.dolphinSchedulerToken();
        return DolphinSchedulerClient.builder()
                                     .host(host)
                                     .port(port)
                                     //.username(username)
                                     //.password(password)
                                     .token(token)
                                     .useHttps(true)
                                     .trustAllCertificates(true)
                                     .enableLogging(true)
                                     .build();
    }
}