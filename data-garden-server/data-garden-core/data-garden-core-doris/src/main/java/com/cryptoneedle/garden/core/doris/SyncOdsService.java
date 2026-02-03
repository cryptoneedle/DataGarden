package com.cryptoneedle.garden.core.doris;

import cn.hutool.v7.core.bean.BeanUtil;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
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
public class SyncOdsService {
    
    public final SyncOdsService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public SyncOdsService(@Lazy SyncOdsService service,
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
        List<OdsTable> originList = select.ods.tables();
        List<DorisTable> dorisDealList = select.doris.tables(select.config.dorisSchemaOds());
        
        // 查询所有
        Map<DorisTableKey, OdsTable> originMap = Maps.uniqueIndex(originList, OdsTable::getId);
        
        // 待同步数据
        Map<DorisTableKey, DorisTable> dealMap = Maps.uniqueIndex(dorisDealList, DorisTable::getId);
        List<OdsTable> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, OdsTable.class))
                .toList();
        
        // 新增数据
        List<OdsTable> addList = dealList.stream()
                                           .filter(deal -> !originMap.containsKey(deal.getId()))
                                           .toList();
        
        // 移除数据
        List<OdsTable> removeList = originList.stream()
                                                .filter(item -> !dealMap.containsKey(item.getId()))
                                                .toList();
        
        // 保存数据
        List<OdsTable> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisTable deal = dealMap.get(origin.getId());
                    if (deal != null) {
                    
                    }
                }).toList();
        
        add.ods.tables(addList);
        save.ods.tables(saveList);
        delete.ods.tables(removeList);
        
        service.syncColumn();
    }
    
    public void syncColumn() {
        List<OdsColumn> originList = select.ods.columns();
        List<DorisColumn> dorisDealList = select.doris.columns(select.config.dorisSchemaOds());
        
        // 查询所有
        Map<DorisColumnKey, OdsColumn> originMap = Maps.uniqueIndex(originList, OdsColumn::getId);
        
        // 待同步数据
        Map<DorisColumnKey, DorisColumn> dealMap = Maps.uniqueIndex(dorisDealList, DorisColumn::getId);
        List<OdsColumn> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, OdsColumn.class))
                .toList();
        
        // 新增数据
        List<OdsColumn> addList = dealList.stream()
                                         .filter(deal -> !originMap.containsKey(deal.getId()))
                                         .toList();
        
        // 移除数据
        List<OdsColumn> removeList = originList.stream()
                                              .filter(item -> !dealMap.containsKey(item.getId()))
                                              .toList();
        
        // 保存数据
        List<OdsColumn> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisColumn deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setSort(deal.getSort());
                    }
                }).toList();
        
        add.ods.columns(addList);
        save.ods.columns(saveList);
        delete.ods.columns(removeList);
    }
}