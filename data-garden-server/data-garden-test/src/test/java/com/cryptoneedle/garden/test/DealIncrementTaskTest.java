package com.cryptoneedle.garden.test;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-03-17
 */
import java.util.*;
import java.util.stream.Collectors;

public class DealIncrementTaskTest {
    
    public static void main(String[] args) {
        System.out.println("========== 开始测试 DealIncrementTask 方法 ==========\n");
        
        // 测试1: 基本分组测试
        testBasicGrouping();
        
        // 测试2: 恰好20个表
        testExactlyMaxNum();
        
        // 测试3: 已有分组的补充
        testPreAssignedGroups();
        
        // 测试4: 超量重新分配
        testOverflowReassignment();
        
        // 测试5: 多个采集频率
        testMultipleFrequencies();
        
        // 测试6: 复杂场景
        testComplexScenario();
        
        // 测试7: 空列表
        testEmptyList();
        
        System.out.println("\n========== 所有测试完成 ==========");
    }
    
    /**
     * 测试1: 基本分组 - 25个表分成2组（20+5）
     */
    private static void testBasicGrouping() {
        System.out.println("【测试1】基本分组测试：25个表 -> 应分成20+5");
        System.out.println("----------------------------------------");
        
        List<SourceTable> tables = createTables(25, "DAILY", 8, null);
        Map<Integer, List<SourceTable>> result = processTablesForTest(tables, 20);
        
        System.out.println("总组数: " + result.size());
        result.forEach((groupNum, tableList) -> {
            System.out.println("  组" + groupNum + ": " + tableList.size() + "个表");
            tableList.forEach(t -> System.out.println("    - " + t.getTableName() + " (groupNum=" + t.getCollectGroupNum() + ")"));
        });
        
        // 断言
        boolean pass = result.size() == 2 &&
                result.get(1).size() == 20 &&
                result.get(2).size() == 5;
        System.out.println("测试结果: " + (pass ? "✅ PASS" : "❌ FAIL"));
        System.out.println();
    }
    
    /**
     * 测试2: 恰好20个表
     */
    private static void testExactlyMaxNum() {
        System.out.println("【测试2】恰好maxNum个表：20个表 -> 应该只有1组");
        System.out.println("----------------------------------------");
        
        List<SourceTable> tables = createTables(20, "DAILY", 8, null);
        Map<Integer, List<SourceTable>> result = processTablesForTest(tables, 20);
        
        System.out.println("总组数: " + result.size());
        result.forEach((groupNum, tableList) -> {
            System.out.println("  组" + groupNum + ": " + tableList.size() + "个表");
        });
        
        boolean pass = result.size() == 1 && result.get(1).size() == 20;
        System.out.println("测试结果: " + (pass ? "✅ PASS" : "❌ FAIL"));
        System.out.println();
    }
    
    /**
     * 测试3: 已有分组的补充
     */
    private static void testPreAssignedGroups() {
        System.out.println("【测试3】已有分组的补充：组1已有10个，新增15个 -> 组1补充到20个，组2有5个");
        System.out.println("----------------------------------------");
        
        List<SourceTable> tables = new ArrayList<>();
        tables.addAll(createTables(10, "DAILY", 8, 1));  // 已分配到组1
        tables.addAll(createTables(15, "DAILY", 8, null)); // 未分配
        
        System.out.println("初始状态:");
        System.out.println("  已分配到组1: 10个表");
        System.out.println("  未分配: 15个表");
        System.out.println();
        
        Map<Integer, List<SourceTable>> result = processTablesForTest(tables, 20);
        
        System.out.println("处理后:");
        System.out.println("总组数: " + result.size());
        result.forEach((groupNum, tableList) -> {
            System.out.println("  组" + groupNum + ": " + tableList.size() + "个表");
        });
        
        boolean pass = result.size() == 2 &&
                result.get(1).size() == 20 &&
                result.get(2).size() == 5;
        System.out.println("测试结果: " + (pass ? "✅ PASS" : "❌ FAIL"));
        System.out.println();
    }
    
    /**
     * 测试4: 超量重新分配
     */
    private static void testOverflowReassignment() {
        System.out.println("【测试4】超量重新分配：组1已有25个表（超过maxNum=20）");
        System.out.println("----------------------------------------");
        
        List<SourceTable> tables = createTables(25, "DAILY", 8, 1);
        
        System.out.println("初始状态:");
        System.out.println("  组1: 25个表（超量）");
        System.out.println();
        
        Map<Integer, List<SourceTable>> result = processTablesForTest(tables, 20);
        
        System.out.println("处理后:");
        System.out.println("总组数: " + result.size());
        result.forEach((groupNum, tableList) -> {
            System.out.println("  组" + groupNum + ": " + tableList.size() + "个表");
        });
        
        boolean pass = result.size() == 2 &&
                result.get(1).size() == 20 &&
                result.get(2).size() == 5;
        System.out.println("测试结果: " + (pass ? "✅ PASS" : "❌ FAIL"));
        System.out.println();
    }
    
    /**
     * 测试5: 多个采集频率
     */
    private static void testMultipleFrequencies() {
        System.out.println("【测试5】多个采集频率：DAILY_8有25个表，HOURLY_12有15个表");
        System.out.println("----------------------------------------");
        
        List<SourceTable> tables = new ArrayList<>();
        tables.addAll(createTables(25, "DAILY", 8, null));
        tables.addAll(createTables(15, "HOURLY", 12, null));
        
        System.out.println("初始状态:");
        System.out.println("  DAILY_8: 25个表");
        System.out.println("  HOURLY_12: 15个表");
        System.out.println();
        
        Map<String, Map<Integer, List<SourceTable>>> result = processTablesGroupedByFrequency(tables, 20);
        
        System.out.println("处理后:");
        result.forEach((frequency, subGroups) -> {
            System.out.println("频率 " + frequency + ":");
            subGroups.forEach((groupNum, tableList) -> {
                System.out.println("    组" + groupNum + ": " + tableList.size() + "个表");
            });
        });
        
        Map<Integer, List<SourceTable>> dailyGroups = result.get("DAILY_8");
        Map<Integer, List<SourceTable>> hourlyGroups = result.get("HOURLY_12");
        
        boolean pass = result.size() == 2 &&
                dailyGroups.size() == 2 &&
                dailyGroups.get(1).size() == 20 &&
                dailyGroups.get(2).size() == 5 &&
                hourlyGroups.size() == 1 &&
                hourlyGroups.get(1).size() == 15;
        System.out.println("测试结果: " + (pass ? "✅ PASS" : "❌ FAIL"));
        System.out.println();
    }
    
    /**
     * 测试6: 复杂场景
     */
    private static void testComplexScenario() {
        System.out.println("【测试6】复杂场景：组1有18个，组2有25个（超量），未分配10个");
        System.out.println("----------------------------------------");
        
        List<SourceTable> tables = new ArrayList<>();
        tables.addAll(createTables(18, "DAILY", 8, 1));
        tables.addAll(createTables(25, "DAILY", 8, 2));
        tables.addAll(createTables(10, "DAILY", 8, null));
        
        System.out.println("初始状态:");
        System.out.println("  组1: 18个表");
        System.out.println("  组2: 25个表（超量）");
        System.out.println("  未分配: 10个表");
        System.out.println();
        
        Map<Integer, List<SourceTable>> result = processTablesForTest(tables, 20);
        
        System.out.println("处理后:");
        System.out.println("总组数: " + result.size());
        result.forEach((groupNum, tableList) -> {
            System.out.println("  组" + groupNum + ": " + tableList.size() + "个表");
        });
        
        System.out.println("\n预期分配:");
        System.out.println("  组1: 18 + 2(未分配) = 20");
        System.out.println("  组2: 20（保留前20个，5个溢出）");
        System.out.println("  组3: 5(溢出) + 8(未分配) = 13");
        
        boolean pass = result.size() == 3 &&
                result.get(1).size() == 20 &&
                result.get(2).size() == 20 &&
                result.get(3).size() == 13;
        System.out.println("测试结果: " + (pass ? "✅ PASS" : "❌ FAIL"));
        System.out.println();
    }
    
    /**
     * 测试7: 空列表
     */
    private static void testEmptyList() {
        System.out.println("【测试7】空列表测试");
        System.out.println("----------------------------------------");
        
        List<SourceTable> tables = new ArrayList<>();
        Map<Integer, List<SourceTable>> result = processTablesForTest(tables, 20);
        
        System.out.println("总组数: " + result.size());
        
        boolean pass = result.isEmpty();
        System.out.println("测试结果: " + (pass ? "✅ PASS" : "❌ FAIL"));
        System.out.println();
    }
    
    // ========== 核心处理逻辑（模拟原方法） ==========
    
    private static Map<Integer, List<SourceTable>> processTablesForTest(
            List<SourceTable> tables, int maxNum) {
        
        Map<Integer, List<SourceTable>> subGroupMap = new HashMap<>();
        List<SourceTable> unassignedTables = new ArrayList<>();
        
        // 1. 统计已分配的表
        for (SourceTable table : tables) {
            Integer gNum = table.getCollectGroupNum();
            if (gNum != null && gNum > 0) {
                subGroupMap.computeIfAbsent(gNum, k -> new ArrayList<>()).add(table);
            } else {
                unassignedTables.add(table);
            }
        }
        
        // 2. 处理超量的组
        for (Map.Entry<Integer, List<SourceTable>> entry : subGroupMap.entrySet()) {
            List<SourceTable> currentSubGroup = entry.getValue();
            if (currentSubGroup.size() > maxNum) {
                List<SourceTable> overflow = new ArrayList<>(
                        currentSubGroup.subList(maxNum, currentSubGroup.size())
                );
                unassignedTables.addAll(overflow);
                currentSubGroup.subList(maxNum, currentSubGroup.size()).clear();
            }
        }
        
        // 3. 分配未分配的表
        if (!unassignedTables.isEmpty()) {
            int currentGroupNum = 1;
            int unassignedIdx = 0;
            
            while (unassignedIdx < unassignedTables.size()) {
                while (subGroupMap.containsKey(currentGroupNum)
                        && subGroupMap.get(currentGroupNum).size() >= maxNum) {
                    currentGroupNum++;
                }
                
                List<SourceTable> currentTargetGroup =
                        subGroupMap.computeIfAbsent(currentGroupNum, k -> new ArrayList<>());
                
                while (currentTargetGroup.size() < maxNum
                        && unassignedIdx < unassignedTables.size()) {
                    SourceTable table = unassignedTables.get(unassignedIdx++);
                    table.setCollectGroupNum(currentGroupNum);
                    currentTargetGroup.add(table);
                }
                
                currentGroupNum++;
            }
        }
        
        return subGroupMap;
    }
    
    private static Map<String, Map<Integer, List<SourceTable>>> processTablesGroupedByFrequency(
            List<SourceTable> tables, int maxNum) {
        
        Map<String, List<SourceTable>> baseGroupMap = tables.stream()
                                                            .collect(Collectors.groupingBy(t ->
                                                                                                   t.getCollectFrequency() + "_" + t.getCollectTimePoint()
                                                            ));
        
        Map<String, Map<Integer, List<SourceTable>>> result = new HashMap<>();
        
        baseGroupMap.forEach((key, groupTables) -> {
            result.put(key, processTablesForTest(groupTables, maxNum));
        });
        
        return result;
    }
    
    // ========== 辅助方法 ==========
    
    private static List<SourceTable> createTables(int count, String frequency,
                                                  int timePoint, Integer groupNum) {
        List<SourceTable> tables = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SourceTable table = new SourceTable();
            table.setTableName("table_" + frequency + "_" + timePoint + "_" +
                                       String.format("%03d", i));
            table.setCollectFrequency(frequency);
            table.setCollectTimePoint(timePoint);
            table.setCollectGroupNum(groupNum);
            tables.add(table);
        }
        return tables;
    }
    
    // ========== Mock类 ==========
    
    static class SourceTable {
        private String tableName;
        private String collectFrequency;
        private Integer collectTimePoint;
        private Integer collectGroupNum;
        
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        public String getCollectFrequency() { return collectFrequency; }
        public void setCollectFrequency(String collectFrequency) {
            this.collectFrequency = collectFrequency;
        }
        public Integer getCollectTimePoint() { return collectTimePoint; }
        public void setCollectTimePoint(Integer collectTimePoint) {
            this.collectTimePoint = collectTimePoint;
        }
        public Integer getCollectGroupNum() { return collectGroupNum; }
        public void setCollectGroupNum(Integer collectGroupNum) {
            this.collectGroupNum = collectGroupNum;
        }
    }
}