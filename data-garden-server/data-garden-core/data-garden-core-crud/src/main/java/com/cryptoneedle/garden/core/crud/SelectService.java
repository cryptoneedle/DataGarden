package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.config.SelectConfigService;
import com.cryptoneedle.garden.core.crud.doris.SelectDorisService;
import com.cryptoneedle.garden.core.crud.source.SelectSourceService;
import org.springframework.stereotype.Service;

/**
 * <p>description: 查询数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class SelectService {

    public final SelectConfigService config;
    public final SelectSourceService source;
    public final SelectDorisService doris;

    public SelectService(SelectConfigService config,
                         SelectSourceService source,
                         SelectDorisService doris) {
        this.config = config;
        this.source = source;
        this.doris = doris;
    }
}