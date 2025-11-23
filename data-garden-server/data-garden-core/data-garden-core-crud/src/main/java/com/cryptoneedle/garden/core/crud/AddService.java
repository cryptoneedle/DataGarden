package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.config.AddConfigService;
import com.cryptoneedle.garden.core.crud.doris.AddDorisService;
import com.cryptoneedle.garden.core.crud.source.AddSourceService;
import org.springframework.stereotype.Service;

/**
 * <p>description: 新增数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class AddService {

    public final AddConfigService config;
    public final AddSourceService source;
    public final AddDorisService doris;

    public AddService(AddConfigService config,
                      AddSourceService source,
                      AddDorisService doris) {
        this.config = config;
        this.source = source;
        this.doris = doris;
    }
}