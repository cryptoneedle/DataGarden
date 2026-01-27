package com.cryptoneedle.garden.core.ds;

import com.cryptoneedle.garden.core.crud.SelectService;
import com.cryptoneedle.garden.core.source.SourceService;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceCatalog;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceDatabase;
import com.cryptoneedle.garden.infrastructure.entity.source.SourceTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.client.DolphinSchedulerClient;
import org.apache.dolphinscheduler.client.model.builder.TaskDefinitionBuilder;
import org.apache.dolphinscheduler.client.model.builder.WorkflowBuilder;
import org.apache.dolphinscheduler.client.model.request.WorkflowCreateRequest;
import org.apache.dolphinscheduler.client.model.response.WorkflowResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-27
 */
@Slf4j
@Service
public class DolphinSchedulerService {
    
    public final SelectService select;
    public final DolphinSchedulerClient dolphinSchedulerClient;
    public final SourceService sourceService;
    
    
    public DolphinSchedulerService(SelectService select,
                                   DolphinSchedulerClient dolphinSchedulerClient,
                                   @Lazy SourceService sourceService) {
        this.select = select;
        this.dolphinSchedulerClient = dolphinSchedulerClient;
        this.sourceService = sourceService;
    }
    
    public void dealFullTask(SourceCatalog catalog, SourceDatabase database) {
        List<SourceTable> tables = select.source.tables(catalog.getId().getCatalogName(), database.getId().getDatabaseName());
        for (SourceTable table : tables) {
            dealFullTask(catalog, database, table);
        }
    }
    
    public void dealFullTask(SourceCatalog catalog, SourceDatabase database, SourceTable table) {
        String seatunnelScript = sourceService.createSeatunnelScript(catalog, database, table);
        
        TaskDefinitionBuilder.SeatunnelTaskParams seatunnelTaskParams = TaskDefinitionBuilder.SeatunnelTaskParams.builder()
                                                                                                                 .useZetaEngine()
                                                                                                                 .localMode()
                                                                                                                 .rawScript(seatunnelScript)
                                                                                                                 .build();
        WorkflowCreateRequest request = WorkflowBuilder.builder()
                                                       .name(genWorkFlowName(table))
                                                       .description(table.getTransTableName())
                                                       .addSeatunnelTask(genWorkFlowName(table), seatunnelTaskParams)
                                                       .linearFlow()
                                                       .autoLayout(50, 50, 250, 100, 4)
                                                       .tenantCode(select.config.dolphinSchedulerTenantCode())
                                                       .build();
        WorkflowResponse response;
        
        Long projectCode = select.config.dolphinSchedulerProjectFull();
        Long workFlowCode = table.getDsFullWorkFlow();
        if (workFlowCode == null) {
            // 新增
            response = dolphinSchedulerClient.workflow().createWorkflow(projectCode, request);
            table.setDsFullWorkFlow(response.getCode());
        } else {
            try {
                dolphinSchedulerClient.workflow().queryWorkflow(projectCode, workFlowCode);
                // 更新
                response = dolphinSchedulerClient.workflow().updateWorkflow(projectCode, workFlowCode, request);
                table.setDsFullWorkFlow(response.getCode());
            } catch (Exception e) {
                // 新增
                response = dolphinSchedulerClient.workflow().createWorkflow(projectCode, request);
                table.setDsFullWorkFlow(response.getCode());
            }
        }
    }
    
    private String genWorkFlowName(SourceTable table) {
        return "[%s]%s.%s->%s".formatted(table.getId().getCatalogName(), table.getId().getDatabaseName(), table.getId().getTableName(), table.getTransTableName());
    }
}