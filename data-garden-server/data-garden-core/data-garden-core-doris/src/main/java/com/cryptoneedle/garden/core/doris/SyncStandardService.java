package com.cryptoneedle.garden.core.doris;

import cn.hutool.v7.core.bean.BeanUtil;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardColumn;
import com.cryptoneedle.garden.infrastructure.entity.standard.StandardTable;
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
public class SyncStandardService {
    
    public final SyncStandardService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public SyncStandardService(@Lazy SyncStandardService service,
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
        List<StandardTable> originList = select.standard.tables();
        List<DorisTable> dorisDealList = select.doris.tables(select.config.dorisSchemaStandard());
        
        // 查询所有
        Map<DorisTableKey, StandardTable> originMap = Maps.uniqueIndex(originList, StandardTable::getId);
        
        // 待同步数据
        Map<DorisTableKey, DorisTable> dealMap = Maps.uniqueIndex(dorisDealList, DorisTable::getId);
        List<StandardTable> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, StandardTable.class))
                .toList();
        
        // 新增数据
        List<StandardTable> addList = dealList.stream()
                                           .filter(deal -> !originMap.containsKey(deal.getId()))
                                           .toList();
        
        // 移除数据
        List<StandardTable> removeList = originList.stream()
                                                .filter(item -> !dealMap.containsKey(item.getId()))
                                                .toList();
        
        // 保存数据
        List<StandardTable> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisTable deal = dealMap.get(origin.getId());
                    if (deal != null) {
                    
                    }
                }).toList();
        
        add.standard.tables(addList);
        save.standard.tables(saveList);
        delete.standard.tables(removeList);
        
        service.syncColumn();
    }
    
    public void syncColumn() {
        List<StandardColumn> originList = select.standard.columns();
        List<DorisColumn> dorisDealList = select.doris.columns(select.config.dorisSchemaStandard());
        
        // 查询所有
        Map<DorisColumnKey, StandardColumn> originMap = Maps.uniqueIndex(originList, StandardColumn::getId);
        
        // 待同步数据
        Map<DorisColumnKey, DorisColumn> dealMap = Maps.uniqueIndex(dorisDealList, DorisColumn::getId);
        List<StandardColumn> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, StandardColumn.class))
                .toList();
        
        // 新增数据
        List<StandardColumn> addList = dealList.stream()
                                         .filter(deal -> !originMap.containsKey(deal.getId()))
                                         .toList();
        
        // 移除数据
        List<StandardColumn> removeList = originList.stream()
                                              .filter(item -> !dealMap.containsKey(item.getId()))
                                              .toList();
        
        // 保存数据
        List<StandardColumn> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisColumn deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setSort(deal.getSort());
                    }
                }).toList();
        
        add.standard.columns(addList);
        save.standard.columns(saveList);
        delete.standard.columns(removeList);
    }
}