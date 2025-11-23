package com.cryptoneedle.garden.core.crud;

import com.cryptoneedle.garden.core.crud.config.PatchConfigService;
import com.cryptoneedle.garden.core.crud.doris.PatchDorisService;
import com.cryptoneedle.garden.core.crud.source.PatchSourceService;
import org.springframework.stereotype.Service;

/**
 * <p>description: 部分更新数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
public class PatchService {

    public final PatchConfigService config;
    public final PatchSourceService source;
    public final PatchDorisService doris;

    public PatchService(PatchConfigService config,
                        PatchSourceService source,
                        PatchDorisService doris) {
        this.config = config;
        this.source = source;
        this.doris = doris;
    }
}