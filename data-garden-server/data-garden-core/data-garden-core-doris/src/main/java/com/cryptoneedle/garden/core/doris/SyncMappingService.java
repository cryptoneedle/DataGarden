package com.cryptoneedle.garden.core.doris;

import cn.hutool.v7.core.bean.BeanUtil;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumn;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-02-03
 */
@Slf4j
@Service
public class SyncMappingService {
    
    public final SyncMappingService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public SyncMappingService(@Lazy SyncMappingService service,
                              AddService addService,
                              SelectService selectService,
                              SaveService saveService,
                              DeleteService deleteService,
                              PatchService patchService) {
        this.service = service;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
    }
    
    public void syncTable() {
        List<MappingTable> originList = select.mapping.tables();
        List<DorisTable> dorisDealList = select.doris.tables(select.config.dorisSchemaMapping());
        
        // 查询所有
        Map<DorisTableKey, MappingTable> originMap = Maps.uniqueIndex(originList, MappingTable::getId);
        
        // 待同步数据
        Map<DorisTableKey, DorisTable> dealMap = Maps.uniqueIndex(dorisDealList, DorisTable::getId);
        List<MappingTable> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, MappingTable.class))
                .toList();
        
        // 新增数据
        List<MappingTable> addList = dealList.stream()
                                           .filter(deal -> !originMap.containsKey(deal.getId()))
                                           .toList();
        
        // 移除数据
        List<MappingTable> removeList = originList.stream()
                                                .filter(item -> !dealMap.containsKey(item.getId()))
                                                .toList();
        
        // 保存数据
        List<MappingTable> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisTable deal = dealMap.get(origin.getId());
                    if (deal != null) {
                    
                    }
                }).toList();
        
        add.mapping.tables(addList);
        save.mapping.tables(saveList);
        delete.mapping.tables(removeList);
        
        service.syncColumn();
    }
    
    public void syncColumn() {
        List<MappingColumn> originList = select.mapping.columns();
        List<DorisColumn> dorisDealList = select.doris.columns(select.config.dorisSchemaMapping());
        
        // 查询所有
        Map<DorisColumnKey, MappingColumn> originMap = Maps.uniqueIndex(originList, MappingColumn::getId);
        
        // 待同步数据
        Map<DorisColumnKey, DorisColumn> dealMap = Maps.uniqueIndex(dorisDealList, DorisColumn::getId);
        List<MappingColumn> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, MappingColumn.class))
                .toList();
        
        // 新增数据
        List<MappingColumn> addList = dealList.stream()
                                         .filter(deal -> !originMap.containsKey(deal.getId()))
                                         .toList();
        
        // 移除数据
        List<MappingColumn> removeList = originList.stream()
                                              .filter(item -> !dealMap.containsKey(item.getId()))
                                              .toList();
        
        // 保存数据
        List<MappingColumn> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisColumn deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setSort(deal.getSort());
                    }
                }).toList();
        
        add.mapping.columns(addList);
        save.mapping.columns(saveList);
        delete.mapping.columns(removeList);
    }
}