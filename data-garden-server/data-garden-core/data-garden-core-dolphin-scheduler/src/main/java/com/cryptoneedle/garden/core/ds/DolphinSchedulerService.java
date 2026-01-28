package com.cryptoneedle.garden.core.ds;

import cn.hutool.v7.core.date.DateUtil;
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
        List<SourceTable> tables = select.source.tablesEnabled(catalog.getId().getCatalogName(), database.getId().getDatabaseName());
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
        
        // CollectGroupNum 为分组采集队列，默认为0
        // 在分配过程中以参数 dolphin_scheduler_workflow_task_num 个为一组进行分配
        Integer maxNum = select.config.dolphinSchedulerWorkflowTaskNum();
        // 对于以下情况需要重新计算 CollectGroupNum
        // 1.CollectGroupNum 所在队列总量超出 maxNum
        // 2.CollectGroupNum = 0，表示未进行分配
        
        // 1.按照同一采集时间分组
        Map<String, List<SourceTable>> baseGroupMap = tables.stream()
                                                            .collect(Collectors.groupingBy(t ->
                                                                    t.getCollectFrequency() + "_" + t.getCollectTimePoint()
                                                            ));
        
        baseGroupMap.forEach((key, groupTables) -> {
            // 2. 统计每个队列的各个 collectGroupNum 的占用情况
            // Map<GroupNum, List<Tables>>
            Map<Integer, List<SourceTable>> subGroupMap = new HashMap<>();
            List<SourceTable> unassignedTables = new ArrayList<>();
            for (SourceTable table : groupTables) {
                Integer gNum = table.getCollectGroupNum();
                if (gNum != null && gNum > 0) {
                    subGroupMap.computeIfAbsent(gNum, k -> new ArrayList<>()).add(table);
                } else {
                    unassignedTables.add(table);
                }
            }
            
            // 3. CollectGroupNum 所在队列总量超出 maxNum，多出的移入 unassignedTables
            for (Map.Entry<Integer, List<SourceTable>> entry : subGroupMap.entrySet()) {
                List<SourceTable> currentSubGroup = entry.getValue();
                if (currentSubGroup.size() > maxNum) {
                    // 保留前 maxNum 个，剩余的加入待分配队列
                    List<SourceTable> overflow = new ArrayList<>(currentSubGroup.subList(maxNum, currentSubGroup.size()));
                    unassignedTables.addAll(overflow);
                    
                    // 截断原列表
                    currentSubGroup.subList(maxNum, currentSubGroup.size()).clear();
                }
            }
            // 4. 分配待处理的表（unassignedTables）
            if (!unassignedTables.isEmpty()) {
                int currentGroupNum = 1;
                int unassignedIdx = 0;
                while (unassignedIdx < unassignedTables.size()) {
                    List<SourceTable> currentTargetGroup = subGroupMap.computeIfAbsent(currentGroupNum, k -> new ArrayList<>());
                    
                    // 如果当前组还没满，就往里塞
                    while (currentTargetGroup.size() < maxNum && unassignedIdx < unassignedTables.size()) {
                        SourceTable table = unassignedTables.get(unassignedIdx++);
                        table.setCollectGroupNum(currentGroupNum);
                        currentTargetGroup.add(table);
                    }
                    // 尝试下一个编号
                    currentGroupNum++;
                }
            }
            
            // subGroupMap
            subGroupMap.forEach((collectGroupNum, subGroup) -> {
                if (collectGroupNum > 0) {
                    SourceTable table = subGroup.getFirst();
                    SourceCollectFrequencyType collectFrequency = table.getCollectFrequency();
                    Integer collectTimePoint = table.getCollectTimePoint();
                    service.dealIncrementTask(catalog, database, collectFrequency, collectTimePoint, collectGroupNum, subGroup);
                }
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
            return "0 %s * ? * SUN *".formatted(collectTimePoint);
        } else if (SourceCollectFrequencyType.FIVE_MINUTE.equals(collectFrequency)) {
            return "0 %s/5 * ? * SUN *".formatted(collectTimePoint % 5);
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