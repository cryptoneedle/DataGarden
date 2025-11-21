package com.cryptoneedle.garden.core.crud;

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

    public final AddSourceService source;
    public final AddDorisService doris;

    public AddService(AddSourceService source,
                      AddDorisService doris) {
        this.source = source;
        this.doris = doris;
    }
}