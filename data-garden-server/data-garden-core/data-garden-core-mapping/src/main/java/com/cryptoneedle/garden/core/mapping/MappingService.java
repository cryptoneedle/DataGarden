package com.cryptoneedle.garden.core.mapping;

import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.core.doris.*;
import com.cryptoneedle.garden.infrastructure.doris.DorisMetadataRepository;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumnRely;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTableRely;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Slf4j
@Service
public class MappingService {
    
    public final MappingService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public MappingService(@Lazy MappingService mappingService,
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
    
    public void relyTable(MappingTableRely mappingTableRely) {
        add.mapping.tableRely(mappingTableRely);
    }
    
    public void relyColumn(MappingColumnRely mappingColumnRely) {
        add.mapping.columnRely(mappingColumnRely);
    }
    
    public void unrelyTable(MappingTableRely mappingTableRely) {
        delete.mapping.tableRely(mappingTableRely);
        delete.mapping.columnRelyByTable(mappingTableRely);
    }
    
    public void unrelyColumn(MappingColumnRely mappingColumnRely) {
        delete.mapping.columnRely(mappingColumnRely);
    }
}