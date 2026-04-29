package com.cryptoneedle.garden.core.ods;

import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.vo.ods.OdsColumnVo;
import com.cryptoneedle.garden.infrastructure.vo.ods.OdsTableVo;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Service
public class OdsService {
    
    public final OdsService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public OdsService(@Lazy OdsService mappingService,
                          AddService addService,
                          SelectService selectService,
                          SaveService saveService,
                          DeleteService deleteService,
                          PatchService patchService) {
        this.service = mappingService;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
    }
    
    public Page<OdsTableVo> tableVos(Pageable pageable, String tableName) {
        return select.ods.tableVos(pageable, tableName);
    }
    
    public List<OdsColumnVo> columnVos(String tableName) {
        return select.ods.columnVos(tableName);
    }
}