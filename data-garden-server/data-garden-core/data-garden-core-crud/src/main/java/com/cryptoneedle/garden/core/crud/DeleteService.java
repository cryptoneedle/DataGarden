package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.config.DeleteConfigService;
import com.cryptoneedle.garden.core.crud.doris.DeleteDorisService;
import com.cryptoneedle.garden.core.crud.source.DeleteSourceService;
import org.springframework.stereotype.Service;

/**
 * <p>description: 删除数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class DeleteService {

    public final DeleteConfigService config;
    public final DeleteSourceService source;
    public final DeleteDorisService doris;

    public DeleteService(DeleteConfigService config,
                         DeleteSourceService source,
                         DeleteDorisService doris) {
        this.config = config;
        this.source = source;
        this.doris = doris;
    }
}