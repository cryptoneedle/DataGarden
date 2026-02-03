package com.cryptoneedle.garden.core.doris;

import cn.hutool.v7.core.bean.BeanUtil;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsColumn;
import com.cryptoneedle.garden.infrastructure.entity.dws.DwsTable;
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
public class SyncDwsService {
    
    public final SyncDwsService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public SyncDwsService(@Lazy SyncDwsService service,
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
        List<DwsTable> originList = select.dws.tables();
        List<DorisTable> dorisDealList = select.doris.tables(select.config.dorisSchemaDws());
        
        // 查询所有
        Map<DorisTableKey, DwsTable> originMap = Maps.uniqueIndex(originList, DwsTable::getId);
        
        // 待同步数据
        Map<DorisTableKey, DorisTable> dealMap = Maps.uniqueIndex(dorisDealList, DorisTable::getId);
        List<DwsTable> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, DwsTable.class))
                .toList();
        
        // 新增数据
        List<DwsTable> addList = dealList.stream()
                                           .filter(deal -> !originMap.containsKey(deal.getId()))
                                           .toList();
        
        // 移除数据
        List<DwsTable> removeList = originList.stream()
                                                .filter(item -> !dealMap.containsKey(item.getId()))
                                                .toList();
        
        // 保存数据
        List<DwsTable> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisTable deal = dealMap.get(origin.getId());
                    if (deal != null) {
                    
                    }
                }).toList();
        
        add.dws.tables(addList);
        save.dws.tables(saveList);
        delete.dws.tables(removeList);
        
        service.syncColumn();
    }
    
    public void syncColumn() {
        List<DwsColumn> originList = select.dws.columns();
        List<DorisColumn> dorisDealList = select.doris.columns(select.config.dorisSchemaDws());
        
        // 查询所有
        Map<DorisColumnKey, DwsColumn> originMap = Maps.uniqueIndex(originList, DwsColumn::getId);
        
        // 待同步数据
        Map<DorisColumnKey, DorisColumn> dealMap = Maps.uniqueIndex(dorisDealList, DorisColumn::getId);
        List<DwsColumn> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, DwsColumn.class))
                .toList();
        
        // 新增数据
        List<DwsColumn> addList = dealList.stream()
                                         .filter(deal -> !originMap.containsKey(deal.getId()))
                                         .toList();
        
        // 移除数据
        List<DwsColumn> removeList = originList.stream()
                                              .filter(item -> !dealMap.containsKey(item.getId()))
                                              .toList();
        
        // 保存数据
        List<DwsColumn> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisColumn deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setSort(deal.getSort());
                    }
                }).toList();
        
        add.dws.columns(addList);
        save.dws.columns(saveList);
        delete.dws.columns(removeList);
    }
}