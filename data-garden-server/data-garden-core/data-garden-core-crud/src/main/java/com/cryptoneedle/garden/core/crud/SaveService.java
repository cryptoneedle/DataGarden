package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.config.SaveConfigService;
import com.cryptoneedle.garden.core.crud.doris.SaveDorisService;
import com.cryptoneedle.garden.core.crud.source.SaveSourceService;
import org.springframework.stereotype.Service;

/**
 * <p>description: 保存数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class SaveService {

    public final SaveConfigService config;
    public final SaveSourceService source;
    public final SaveDorisService doris;

    public SaveService(SaveConfigService config,
                       SaveSourceService source,
                       SaveDorisService doris) {
        this.config = config;
        this.source = source;
        this.doris = doris;
    }
}