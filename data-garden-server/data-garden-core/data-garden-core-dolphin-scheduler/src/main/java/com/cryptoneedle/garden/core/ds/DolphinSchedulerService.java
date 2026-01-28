package com.cryptoneedle.garden.core.ds;

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
import org.apache.dolphinscheduler.client.model.request.WorkflowCreateRequest;
import org.apache.dolphinscheduler.client.model.response.WorkflowResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        service.refreshWorkFlow(projectCode, workFlowName, request, List.of(table));
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
        
        Map<String, List<SourceTable>> tableMap = tables.stream()
                                                        .collect(Collectors.groupingBy(table ->
                                                                genIncrementWorkFlowName(
                                                                        catalog.getId().getCatalogName(),
                                                                        database.getId().getDatabaseName(),
                                                                        table.getCollectFrequency(),
                                                                        table.getCollectTimePoint(),
                                                                        table.getCollectGroupNum()
                                                                )
                                                        ));
        tableMap.values().forEach(tableList -> {
            SourceTable table = tableList.getFirst();
            SourceCollectFrequencyType collectFrequency = table.getCollectFrequency();
            Integer collectTimePoint = table.getCollectTimePoint();
            Integer collectGroupNum = table.getCollectGroupNum();
            service.dealIncrementTask(catalog, database, collectFrequency, collectTimePoint, collectGroupNum, tableList);
        });
    }
    
    public void dealIncrementTask(SourceCatalog catalog, SourceDatabase database, SourceCollectFrequencyType collectFrequency, Integer collectTimePoint, Integer collectGroupNum, List<SourceTable> tables) {
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
        service.refreshWorkFlow(projectCode, taskName, request, tables);
    }
    
    public void refreshWorkFlow(Long projectCode, String workFlowName, WorkflowCreateRequest request, List<SourceTable> tables) {
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
    }
}