package com.cryptoneedle.garden.core.doris;

import cn.hutool.v7.core.bean.BeanUtil;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ads.AdsTable;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisColumn;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTable;
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
public class SyncAdsService {
    
    public final SyncAdsService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public SyncAdsService(@Lazy SyncAdsService service,
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
        List<AdsTable> originList = select.ads.tables();
        List<DorisTable> dorisDealList = select.doris.tables(select.config.dorisSchemaAds());
        
        // 查询所有
        Map<DorisTableKey, AdsTable> originMap = Maps.uniqueIndex(originList, AdsTable::getId);
        
        // 待同步数据
        Map<DorisTableKey, DorisTable> dealMap = Maps.uniqueIndex(dorisDealList, DorisTable::getId);
        List<AdsTable> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, AdsTable.class))
                .toList();
        
        // 新增数据
        List<AdsTable> addList = dealList.stream()
                                           .filter(deal -> !originMap.containsKey(deal.getId()))
                                           .toList();
        
        // 移除数据
        List<AdsTable> removeList = originList.stream()
                                                .filter(item -> !dealMap.containsKey(item.getId()))
                                                .toList();
        
        // 保存数据
        List<AdsTable> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisTable deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        
                    }
                }).toList();
        
        add.ads.tables(addList);
        save.ads.tables(saveList);
        delete.ads.tables(removeList);
        
        service.syncColumn();
    }
    
    public void syncColumn() {
        List<AdsColumn> originList = select.ads.columns();
        List<DorisColumn> dorisDealList = select.doris.columns(select.config.dorisSchemaAds());
        
        // 查询所有
        Map<DorisColumnKey, AdsColumn> originMap = Maps.uniqueIndex(originList, AdsColumn::getId);
        
        // 待同步数据
        Map<DorisColumnKey, DorisColumn> dealMap = Maps.uniqueIndex(dorisDealList, DorisColumn::getId);
        List<AdsColumn> dealList = dorisDealList
                .stream()
                .map(deal -> BeanUtil.copyProperties(deal, AdsColumn.class))
                .toList();
        
        // 新增数据
        List<AdsColumn> addList = dealList.stream()
                                         .filter(deal -> !originMap.containsKey(deal.getId()))
                                         .toList();
        
        // 移除数据
        List<AdsColumn> removeList = originList.stream()
                                              .filter(item -> !dealMap.containsKey(item.getId()))
                                              .toList();
        
        // 保存数据
        List<AdsColumn> saveList = originList
                .stream()
                .filter(origin -> dealMap.containsKey(origin.getId()))
                .peek(origin -> {
                    DorisColumn deal = dealMap.get(origin.getId());
                    if (deal != null) {
                        origin.setSort(deal.getSort());
                    }
                }).toList();
        
        add.ads.columns(addList);
        save.ads.columns(saveList);
        delete.ads.columns(removeList);
    }
}