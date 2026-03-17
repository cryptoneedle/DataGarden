package com.cryptoneedle.garden.core.ds;

import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.core.math.MathUtil;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.core.crud.SelectService;
import com.cryptoneedle.garden.core.source.SourceService;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDatabase;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.client.DolphinSchedulerClient;
import org.apache.dolphinscheduler.client.model.PageInfo;
import org.apache.dolphinscheduler.client.model.builder.TaskDefinitionBuilder;
import org.apache.dolphinscheduler.client.model.builder.WorkflowBuilder;
import org.apache.dolphinscheduler.client.model.request.ScheduleCreateRequest;
import org.apache.dolphinscheduler.client.model.request.WorkflowCreateRequest;
import org.apache.dolphinscheduler.client.model.response.ScheduleResponse;
import org.apache.dolphinscheduler.client.model.response.WorkflowResponse;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-27
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class DolphinSchedulerService {
    
    public final DolphinSchedulerService service;
    public final SelectService select;
    public final DolphinSchedulerClient dolphinSchedulerClient;
    public final SourceService sourceService;
    
    
    public DolphinSchedulerService(@Lazy DolphinSchedulerService dolphinSchedulerService,
                                   SelectService select,
                                   DolphinSchedulerClient dolphinSchedulerClient,
                                   SourceService sourceService) {
        this.service = dolphinSchedulerService;
        this.select = select;
        this.dolphinSchedulerClient = dolphinSchedulerClient;
        this.sourceService = sourceService;
    }
    
    public void dealFullTask(SourceCatalog catalog, SourceDatabase database) {
        List<SourceTable> tables = select.source.tablesByFullCollect(catalog.getId().getCatalogName(), database.getId().getDatabaseName());
        for (SourceTable table : tables) {
            service.dealFullTask(catalog, database, table);
        }
    }
    
    public void dealFullTask(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        String seatunnelScript = sourceService.createSeatunnelScript(catalog, database, table);
        
        TaskDefinitionBuilder.SeatunnelTaskParams seatunnelTaskParams = TaskDefinitionBuilder.SeatunnelTaskParams.builder()
                                                                                                                 .useZetaEngine()
                                                                                                                 .localMode()
                                                                                                                 .rawScript(seatunnelScript)
                                                                                                                 .build();
        String workFlowName = genFullWorkFlowName(table);
        WorkflowCreateRequest request = WorkflowBuilder.builder()
                                                       .name(workFlowName)
                                                       .description(table.getTransTableName())
                                                       .addSeatunnelTask(genTaskName(table), seatunnelTaskParams)
                                                       .linearFlow()
                                                       .autoLayout(50, 50, 250, 100, 4)
                                                       .tenantCode(select.config.dolphinSchedulerTenantCode())
                                                       .build();
        
        Long projectCode = select.config.dolphinSchedulerProjectFull();
        Long workFlowCode = service.refreshWorkFlow(projectCode, workFlowName, request, List.of(table));
        
        // 定时
        ScheduleCreateRequest scheduleCreateRequest = ScheduleCreateRequest.builder()
                                                                           .workflowDefinitionCode(workFlowCode)
                                                                           .startTime(DateUtil.formatDateTime(DateUtil.today()))
                                                                           .endTime(DateUtil.formatDateTime(DateUtil.offsetYear(DateUtil.today(), 100)))
                                                                           .timezoneId("Asia/Shanghai")
                                                                           // 每周天采集
                                                                           .crontab(transFullCorntab(table.getCollectFrequency(), table.getCollectTimePoint()))
                                                                           .tenantCode("root")
                                                                           .build();
        service.refreshSchedules(projectCode, workFlowCode, scheduleCreateRequest);
    }
    
    private String genFullWorkFlowName(SourceTable table) {
        return "[%s]%s.%s->%s".formatted(table.getId().getCatalogName(), table.getId().getDatabaseName(), table.getId().getTableName(), table.getTransTableName());
    }
    
    private String genIncrementWorkFlowName(String catalogName, String databaseName, SourceCollectFrequencyType collectFrequency, Integer collectTimePoint, Integer collectGroupNum) {
        return "[%s]%s-%s-%s-%s".formatted(catalogName, databaseName, collectFrequency, collectTimePoint, collectGroupNum);
    }
    
    private String genTaskName(SourceTable table) {
        return "%s|%s".formatted(table.getTransTableName(), table.getTransComment());
    }
    
    public void dealIncrementTask(SourceCatalog catalog, SourceDatabase database) {
        List<SourceTable> tables = select.source.tablesEnabled(catalog.getId().getCatalogName(), database.getId().getDatabaseName());
        
        Integer maxNum = select.config.dolphinSchedulerWorkflowTaskNum();

        Map<String, List<SourceTable>> baseGroupMap = tables.stream()
                                                            .collect(Collectors.groupingBy(t ->
                                                                    t.getCollectFrequency() + "_" + t.getCollectTimePoint()
                                                            ));
        
        baseGroupMap.forEach((key, groupTables) -> {
            Map<Integer, List<SourceTable>> subGroupMap = new HashMap<>();
            int totalSize = groupTables.size();
            // 计算需要分几组（向上取整）
            int groupCount = (int) Math.ceil((double) totalSize / maxNum);
            
            // 计算每组应该分配的平均数量
            int avgSize = (int) Math.ceil((double) totalSize / groupCount);
            
            int currentIndex = 0;
            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                List<SourceTable> subList = new ArrayList<>();
                
                // 计算当前组应该取的元素数量
                int endIndex = Math.min(currentIndex + avgSize, totalSize);
                
                // 添加元素到当前组
                for (int i = currentIndex; i < endIndex; i++) {
                    subList.add(groupTables.get(i));
                }
                
                subGroupMap.put(groupIndex, subList);
                currentIndex = endIndex;
            }
            
            subGroupMap.forEach((collectGroupNum, subGroup) -> {
                SourceTable table = subGroup.getFirst();
                SourceCollectFrequencyType collectFrequency = table.getCollectFrequency();
                Integer collectTimePoint = table.getCollectTimePoint();
                service.dealIncrementTask(catalog, database, collectFrequency, collectTimePoint, collectGroupNum, subGroup);
            });
        });
    }
    
    public void dealIncrementTask(SourceCatalog catalog, SourceDatabase database, SourceCollectFrequencyType collectFrequency, Integer collectTimePoint, Integer collectGroupNum, List<SourceTable> tables) {
        // 工作流
        String taskName = genIncrementWorkFlowName(catalog.getId().getCatalogName(), database.getId().getDatabaseName(), collectFrequency, collectTimePoint, collectGroupNum);
        WorkflowBuilder workflowBuilder = WorkflowBuilder.builder();
        for (SourceTable sourceTable : tables) {
            String seatunnelScript = sourceService.createSeatunnelScript(catalog, database, sourceTable);
            TaskDefinitionBuilder.SeatunnelTaskParams seatunnelTaskParams = TaskDefinitionBuilder.SeatunnelTaskParams.builder()
                                                                                                                     .useZetaEngine()
                                                                                                                     .localMode()
                                                                                                                     .rawScript(seatunnelScript)
                                                                                                                     .build();
            workflowBuilder.addSeatunnelTask(genTaskName(sourceTable), seatunnelTaskParams);
        }
        WorkflowCreateRequest request = workflowBuilder.name(taskName)
                                                       .linearFlow()
                                                       .autoLayout(50, 50, 250, 80, 5)
                                                       .tenantCode(select.config.dolphinSchedulerTenantCode())
                                                       .build();
        Long projectCode = select.config.dolphinSchedulerProjectIncrement();
        Long workFlowCode = service.refreshWorkFlow(projectCode, taskName, request, tables);
        
        // 定时
        ScheduleCreateRequest scheduleCreateRequest = ScheduleCreateRequest.builder()
                                                                           .workflowDefinitionCode(workFlowCode)
                                                                           .startTime(DateUtil.formatDateTime(DateUtil.today()))
                                                                           .endTime(DateUtil.formatDateTime(DateUtil.offsetYear(DateUtil.today(), 100)))
                                                                           .timezoneId("Asia/Shanghai")
                                                                           .crontab(transIncrementCorntab(collectFrequency, collectTimePoint))
                                                                           .tenantCode("root")
                                                                           .build();
        service.refreshSchedules(projectCode, workFlowCode, scheduleCreateRequest);
    }
    
    private String transFullCorntab(SourceCollectFrequencyType collectFrequency, Integer collectTimePoint) {
        if (SourceCollectFrequencyType.DAY.equals(collectFrequency)) {
            return "0 0 %s ? * SUN *".formatted(collectTimePoint);
        } else if (SourceCollectFrequencyType.HOUR.equals(collectFrequency)) {
            return "0 %s 0 ? * SUN *".formatted(collectTimePoint);
        } else if (SourceCollectFrequencyType.FIVE_MINUTE.equals(collectFrequency)) {
            return "0 %s/5 0 ? * SUN *".formatted(collectTimePoint % 5);
        }
        throw new RuntimeException("未知采集频率配置: collectFrequency:" + collectFrequency + ",collectTimePoint:" + collectTimePoint);
    }
    
    
    private String transIncrementCorntab(SourceCollectFrequencyType collectFrequency, Integer collectTimePoint) {
        if (SourceCollectFrequencyType.DAY.equals(collectFrequency)) {
            return "0 0 %s * * ? *".formatted(collectTimePoint);
        } else if (SourceCollectFrequencyType.HOUR.equals(collectFrequency)) {
            return "0 %s * * * ? *".formatted(collectTimePoint);
        } else if (SourceCollectFrequencyType.FIVE_MINUTE.equals(collectFrequency)) {
            return "0 %s/5 * * * ? *".formatted(collectTimePoint % 5);
        }
        throw new RuntimeException("未知采集频率配置: collectFrequency:" + collectFrequency + ",collectTimePoint:" + collectTimePoint);
    }
    
    public void refreshSchedules(Long projectCode, Long workFlowCode, ScheduleCreateRequest request) {
        Integer id = null;
        ScheduleResponse response;
        PageInfo<ScheduleResponse> pageInfo = dolphinSchedulerClient.schedule().listSchedules(projectCode, 1, 1, workFlowCode);
        if (pageInfo.getTotal() > 0) {
            response = pageInfo.getTotalList().getFirst();
            id = response.getId();
        }
        if (id == null) {
            // 新增定时
            response = dolphinSchedulerClient.schedule().createSchedule(projectCode, request);
            id = response.getId();
        } else {
            // 下线
            dolphinSchedulerClient.schedule().offlineSchedule(projectCode, id);
            // 修改定时
            dolphinSchedulerClient.schedule().updateSchedule(projectCode, id, request);
        }
        // 上线
        dolphinSchedulerClient.schedule().onlineSchedule(projectCode, id);
    }
    
    public Long refreshWorkFlow(Long projectCode, String workFlowName, WorkflowCreateRequest request, List<SourceTable> tables) {
        Long workFlowCode = null;
        WorkflowResponse response;
        PageInfo<WorkflowResponse> pageInfo = dolphinSchedulerClient.workflow().listWorkflows(projectCode, 1, 1, workFlowName);
        if (pageInfo.getTotal() > 0) {
            response = pageInfo.getTotalList().getFirst();
            workFlowCode = response.getCode();
        }
        
        if (workFlowCode == null) {
            // 新增
            response = dolphinSchedulerClient.workflow().createWorkflow(projectCode, request);
            workFlowCode = response.getCode();
        } else {
            // 下线
            dolphinSchedulerClient.workflow().releaseWorkflow(projectCode, workFlowCode, "OFFLINE");
            // 更新
            response = dolphinSchedulerClient.workflow().updateWorkflow(projectCode, workFlowCode, request);
        }
        
        // 上线
        dolphinSchedulerClient.workflow().releaseWorkflow(projectCode, workFlowCode, "ONLINE");
        
        workFlowCode = response.getCode();
        for (SourceTable sourceTable : tables) {
            sourceTable.setDsFullWorkFlow(workFlowCode);
        }
        return workFlowCode;
    }
}