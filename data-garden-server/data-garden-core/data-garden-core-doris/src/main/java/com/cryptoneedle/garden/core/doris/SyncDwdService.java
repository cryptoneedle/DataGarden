package com.cryptoneedle.garden.core.doris;

import cn.hutool.v7.core.bean.BeanUtil;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdColumn;
import com.cryptoneedle.garden.infrastructure.entity.dwd.DwdTable;
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
public class SyncDwdService {
    
    public final SyncDwdService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public SyncDwdService(@Lazy SyncDwdService service,
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
        List<DwdTable> originList = select.dwd.tables();
        List<DorisTable> dorisDealList = select.doris.tables(select.config.dorisSchemaDwd());
        
        // 查询所有
        Map<DorisTableKey, DwdTable> originMap = Maps.uniqueIndex(originList, DwdTable::getId);
        
        // 待同步数据
        Map<DorisTableKey, DorisTable> dealMap = Maps.uniqueIndex(dorisDealList, DorisTable::getId);
        List<DwdTable> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, DwdTable.class))
                .toList();
        
        // 新增数据
        List<DwdTable> addList = dealList.stream()
                                           .filter(deal -> !originMap.containsKey(deal.getId()))
                                           .toList();
        
        // 移除数据
        List<DwdTable> removeList = originList.stream()
                                                .filter(item -> !dealMap.containsKey(item.getId()))
                                                .toList();
        
        // 保存数据
        List<DwdTable> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisTable deal = dealMap.get(origin.getId());
                    if (deal != null) {
                    
                    }
                }).toList();
        
        add.dwd.tables(addList);
        save.dwd.tables(saveList);
        delete.dwd.tables(removeList);
        
        service.syncColumn();
    }
    
    public void syncColumn() {
        List<DwdColumn> originList = select.dwd.columns();
        List<DorisColumn> dorisDealList = select.doris.columns(select.config.dorisSchemaDwd());
        
        // 查询所有
        Map<DorisColumnKey, DwdColumn> originMap = Maps.uniqueIndex(originList, DwdColumn::getId);
        
        // 待同步数据
        Map<DorisColumnKey, DorisColumn> dealMap = Maps.uniqueIndex(dorisDealList, DorisColumn::getId);
        List<DwdColumn> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, DwdColumn.class))
                .toList();
        
        // 新增数据
        List<DwdColumn> addList = dealList.stream()
                                         .filter(deal -> !originMap.containsKey(deal.getId()))
                                         .toList();
        
        // 移除数据
        List<DwdColumn> removeList = originList.stream()
                                              .filter(item -> !dealMap.containsKey(item.getId()))
                                              .toList();
        
        // 保存数据
        List<DwdColumn> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisColumn deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setSort(deal.getSort());
                    }
                }).toList();
        
        add.dwd.columns(addList);
        save.dwd.columns(saveList);
        delete.dwd.columns(removeList);
    }
}