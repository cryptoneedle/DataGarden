package com.cryptoneedle.garden.core.crud;

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

    public final SelectSourceService source;
    public final SelectDorisService doris;

    public SelectService(SelectSourceService source,
                         SelectDorisService doris) {
        this.source = source;
        this.doris = doris;
    }
}