package com.cryptoneedle.garden.infrastructure.vo.lineage;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>description: SQL血缘分析结果VO </p>
 *
 * @author CryptoNeedle
 * @date 2026-05-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlLineageAnalysisResultVo {
    
    @JsonPropertyDescription("SQL中涉及的目标表（INSERT INTO/CREATE TABLE）列表")
    private List<TableInfo> targetTables;
    
    @JsonPropertyDescription("SQL中涉及的源表（FROM/JOIN）列表")
    private List<TableInfo> sourceTables;
    
    @JsonPropertyDescription("表级血缘关系列表")
    private List<TableLineage> tableLineages;
    
    @JsonPropertyDescription("字段级血缘关系列表")
    private List<FieldLineage> fieldLineages;
    
    /**
     * 表信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableInfo {
        
        @JsonPropertyDescription("数据库名称")
        private String databaseName;
        
        @JsonPropertyDescription("表名称")
        private String tableName;
        
        @JsonPropertyDescription("表别名（如果有）")
        private String alias;
    }
    
    /**
     * 表级血缘关系
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableLineage {
        
        @JsonPropertyDescription("目标数据库")
        private String toDatabaseName;
        
        @JsonPropertyDescription("目标表")
        private String toTableName;
        
        @JsonPropertyDescription("来源数据库")
        private String fromDatabaseName;
        
        @JsonPropertyDescription("来源表")
        private String fromTableName;
    }
    
    /**
     * 字段级血缘关系
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldLineage {
        
        @JsonPropertyDescription("目标数据库")
        private String toDatabaseName;
        
        @JsonPropertyDescription("目标表")
        private String toTableName;
        
        @JsonPropertyDescription("目标字段")
        private String toColumnName;
        
        @JsonPropertyDescription("来源数据库")
        private String fromDatabaseName;
        
        @JsonPropertyDescription("来源表")
        private String fromTableName;
        
        @JsonPropertyDescription("来源字段")
        private String fromColumnName;
        
        @JsonPropertyDescription("转换逻辑（如果有）")
        private String transformLogic;
    }
}
