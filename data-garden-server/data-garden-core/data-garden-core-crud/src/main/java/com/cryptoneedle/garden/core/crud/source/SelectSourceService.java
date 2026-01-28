package com.cryptoneedle.garden.core.crud.source;

import cn.hutool.v7.core.tree.MapTree;
import cn.hutool.v7.core.tree.TreeNode;
import cn.hutool.v7.core.tree.TreeNodeConfig;
import cn.hutool.v7.core.tree.TreeUtil;
import com.cryptoneedle.garden.common.dto.SourceTreeDto;
import com.cryptoneedle.garden.common.enums.SourceCollectFrequencyType;
import com.cryptoneedle.garden.common.enums.SourceDimensionType;
import com.cryptoneedle.garden.common.enums.SourceTreeNodeType;
import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.source.*;
import com.cryptoneedle.garden.infrastructure.entity.source.*;
import com.cryptoneedle.garden.infrastructure.repository.source.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>description: 查询数据源数据服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SelectSourceService {
    
    private final SourceCatalogRepository sourceCatalogRepository;
    private final SourceDatabaseRepository sourceDatabaseRepository;
    private final SourceTableRepository sourceTableRepository;
    private final SourceColumnRepository sourceColumnRepository;
    private final SourceDimensionRepository sourceDimensionRepository;
    
    public SelectSourceService(SourceCatalogRepository sourceCatalogRepository,
                               SourceDatabaseRepository sourceDatabaseRepository,
                               SourceTableRepository sourceTableRepository,
                               SourceColumnRepository sourceColumnRepository,
                               SourceDimensionRepository sourceDimensionRepository) {
        this.sourceCatalogRepository = sourceCatalogRepository;
        this.sourceDatabaseRepository = sourceDatabaseRepository;
        this.sourceTableRepository = sourceTableRepository;
        this.sourceColumnRepository = sourceColumnRepository;
        this.sourceDimensionRepository = sourceDimensionRepository;
    }
    
    /**
     * SourceCatalog
     */
    
    public SourceCatalog catalog(SourceCatalogKey id) {
        return sourceCatalogRepository.findById(id).orElse(null);
    }
    
    public SourceCatalog catalog(String catalogName) {
        return catalog(SourceCatalogKey.builder().catalogName(catalogName).build());
    }
    
    public SourceCatalog catalogCheck(SourceCatalogKey id) throws EntityNotFoundException {
        return sourceCatalogRepository.findById(id)
                                      .orElseThrow(() -> new EntityNotFoundException("SourceCatalog", id.toString()));
    }
    
    public SourceCatalog catalogByDoris(String dorisCatalog) {
        return sourceCatalogRepository.catalogByDoris(dorisCatalog);
    }
    
    public List<SourceCatalog> catalogs() {
        return sourceCatalogRepository.catalogs();
    }
    
    public List<SourceCatalog> catalogsEnabled() {
        return sourceCatalogRepository.catalogsEnabled();
    }
    
    public List<SourceCatalog> catalogsByServer(String host, Integer port) {
        return sourceCatalogRepository.catalogsByServer(host, port);
    }
    
    /**
     * SourceDatabase
     */
    
    public SourceDatabase database(SourceDatabaseKey id) {
        return sourceDatabaseRepository.findById(id).orElse(null);
    }
    
    public SourceDatabase databaseCheck(SourceDatabaseKey id) throws EntityNotFoundException {
        return sourceDatabaseRepository.findById(id)
                                       .orElseThrow(() -> new EntityNotFoundException("SourceDatabase", id.toString()));
    }
    
    public List<SourceDatabase> databases() {
        return sourceDatabaseRepository.databases();
    }
    
    public List<SourceDatabase> databases(String catalogName) {
        return sourceDatabaseRepository.databases(catalogName);
    }
    
    public List<SourceDatabase> databasesEnabled() {
        return sourceDatabaseRepository.databasesEnabled();
    }
    
    public List<SourceDatabase> databasesEnabled(String catalogName) {
        return sourceDatabaseRepository.databasesEnabled(catalogName);
    }
    
    /**
     * SourceTable
     */
    
    public SourceTable table(SourceTableKey id) {
        return sourceTableRepository.findById(id).orElse(null);
    }
    
    public SourceTable tableCheck(SourceTableKey id) throws EntityNotFoundException {
        return sourceTableRepository.findById(id)
                                    .orElseThrow(() -> new EntityNotFoundException("SourceTable", id.toString()));
    }
    
    public List<SourceTable> tables() {
        return sourceTableRepository.tables();
    }
    
    public List<SourceTable> tables(String catalogName) {
        return sourceTableRepository.tables(catalogName);
    }
    
    public List<SourceTable> tables(String catalogName, String databaseName) {
        return sourceTableRepository.tables(catalogName, databaseName);
    }
    
    public List<SourceTable> tablesEnabled() {
        return sourceTableRepository.tablesEnabled();
    }
    
    public List<SourceTable> tablesEnabled(String catalogName) {
        return sourceTableRepository.tablesEnabled(catalogName);
    }
    
    public List<SourceTable> tablesEnabled(String catalogName, String databaseName) {
        return sourceTableRepository.tablesEnabled(catalogName, databaseName);
    }
    
    public List<SourceTable> tablesByCollect(String catalogName, String databaseName, SourceCollectFrequencyType collectFrequency, Integer collectTimePoint, Integer collectGroupNum) {
        return sourceTableRepository.tablesByCollect(catalogName, databaseName, collectFrequency, collectTimePoint, collectGroupNum);
    }
    
    /**
     * SourceColumn
     */
    
    public SourceColumn column(SourceColumnKey id) {
        return sourceColumnRepository.findById(id).orElse(null);
    }
    
    public SourceColumn columnCheck(SourceColumnKey id) throws EntityNotFoundException {
        return sourceColumnRepository.findById(id)
                                     .orElseThrow(() -> new EntityNotFoundException("SourceColumn", id.toString()));
    }
    
    public List<SourceColumn> columns() {
        return sourceColumnRepository.columns();
    }
    
    public List<SourceColumn> columns(String catalogName) {
        return sourceColumnRepository.columns(catalogName);
    }
    
    public List<SourceColumn> columns(String catalogName, String databaseName) {
        return sourceColumnRepository.columns(catalogName, databaseName);
    }
    
    public List<SourceColumn> columns(String catalogName, String databaseName, String tableName) {
        return sourceColumnRepository.columns(catalogName, databaseName, tableName);
    }
    
    public List<SourceColumn> columnsEnabled() {
        return sourceColumnRepository.columnsEnabled();
    }
    
    public List<SourceColumn> columnsEnabled(String catalogName) {
        return sourceColumnRepository.columnsEnabled(catalogName);
    }
    
    public List<SourceColumn> columnsEnabled(String catalogName, String databaseName) {
        return sourceColumnRepository.columnsEnabled(catalogName, databaseName);
    }
    
    public List<SourceColumn> columnsEnabled(String catalogName, String databaseName, String tableName) {
        return sourceColumnRepository.columnsEnabled(catalogName, databaseName, tableName);
    }
    
    public List<SourceColumn> columnsWithDimension(SourceTable table) {
        return sourceColumnRepository.columnsWithDimension(table.getId().getCatalogName(), table.getId().getDatabaseName(), table.getId().getTableName());
    }
    
    public List<SourceColumn> columnsWithoutDimension(SourceTable table) {
        return sourceColumnRepository.columnsWithoutDimension(table.getId().getCatalogName(), table.getId().getDatabaseName(), table.getId().getTableName());
    }
    
    public List<SourceColumn> columnsIncremented(String catalogName, String databaseName, String tableName) {
        return sourceColumnRepository.columnsIncremented(catalogName, databaseName, tableName);
    }
    
    /**
     * SourceDimension
     */
    
    public SourceDimension dimension(SourceDimensionKey id) {
        return sourceDimensionRepository.findById(id).orElse(null);
    }
    
    public SourceDimension dimensionCheck(SourceDimensionKey id) throws EntityNotFoundException {
        return sourceDimensionRepository.findById(id)
                                        .orElseThrow(() -> new EntityNotFoundException("SourceDimension", id.toString()));
    }
    
    public List<SourceDimension> dimensions() {
        return sourceDimensionRepository.dimensions();
    }
    
    public List<SourceDimension> dimensions(String catalogName) {
        return sourceDimensionRepository.dimensions(catalogName);
    }
    
    public List<SourceDimension> dimensions(String catalogName, String databaseName) {
        return sourceDimensionRepository.dimensions(catalogName, databaseName);
    }
    
    public List<SourceDimension> dimensions(String catalogName, String databaseName, String tableName) {
        return sourceDimensionRepository.dimensions(catalogName, databaseName, tableName);
    }
    
    public List<SourceDimension> dimensions(String catalogName, String databaseName, String tableName, SourceDimensionType dimensionType) {
        return sourceDimensionRepository.dimensions(catalogName, databaseName, tableName, dimensionType);
    }
    
    public List<SourceDimension> dimensions(String catalogName, String databaseName, String tableName, SourceDimensionType dimensionType, String dimensionName) {
        return sourceDimensionRepository.dimensions(catalogName, databaseName, tableName, dimensionType, dimensionName);
    }
    
    public List<SourceDimension> dimensions(String catalogName, String databaseName, String tableName, String columnName) {
        return sourceDimensionRepository.dimensions(catalogName, databaseName, tableName, columnName);
    }
    
    public List<SourceDimension> dimensionsEnabled(String catalogName, String databaseName, String tableName) {
        return sourceDimensionRepository.dimensionsEnabled(catalogName, databaseName, tableName);
    }
    
    public List<SourceDimension> dimensionsClosed(String catalogName, String databaseName, String tableName) {
        return sourceDimensionRepository.dimensionsClosed(catalogName, databaseName, tableName);
    }
    
    /**
     * Tree
     */
    public List<MapTree<Object>> tree() {
        List<SourceTreeDto> sourceTreeDtos = new ArrayList<>();
        
        // SourceCatalog
        sourceTreeDtos.addAll(catalogs()
                .stream()
                .map(catalog -> new SourceTreeDto()
                        .setId("catalog-" + catalog.getId().getCatalogName())
                        .setParentId(null)
                        .setName(catalog.getId().getCatalogName())
                        .setNodeType(SourceTreeNodeType.CATALOG.name())
                        .setDatabaseType(catalog.getDatabaseType())
                        .setEnabled(catalog.getEnabled())
                        .setActiveDimensionName(null))
                .toList());
        
        // 数据库
        sourceTreeDtos.addAll(databasesEnabled()
                .stream()
                .map(database -> new SourceTreeDto()
                        .setId("database-" + database.getId().getDatabaseName())
                        .setParentId("catalog-" + database.getId().getCatalogName())
                        .setName(database.getId().getDatabaseName())
                        .setNodeType(SourceTreeNodeType.DATABASE.name())
                        .setDatabaseType(null)
                        .setEnabled(database.getEnabled())
                        .setActiveDimensionName(null))
                .toList());
        
        // 表
        sourceTreeDtos.addAll(tablesEnabled()
                .stream()
                .map(table -> new SourceTreeDto()
                        .setId("table-" + table.getId().getTableName())
                        .setParentId("database-" + table.getId().getDatabaseName())
                        .setName(table.getId().getTableName())
                        .setNodeType(SourceTreeNodeType.TABLE.name())
                        .setDatabaseType(null)
                        .setEnabled(table.getEnabled())
                        .setActiveDimensionName(table.getDimension()))
                .toList());
        
        AtomicInteger sort = new AtomicInteger();
        List<TreeNode<String>> treeNodeList = sourceTreeDtos
                .stream()
                .map(treeDto -> new TreeNode<String>()
                        .setId(treeDto.getId())
                        .setParentId(treeDto.getParentId())
                        .setName(treeDto.getName())
                        .setWeight(sort.getAndIncrement())
                        .setExtra(new HashMap<>() {{
                            put("nodeType", treeDto.getNodeType());
                            put("databaseType", treeDto.getDatabaseType());
                            put("enabled", treeDto.getEnabled());
                            put("activeDimensionName", treeDto.getActiveDimensionName());
                        }}))
                .toList();
        
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setNameKey("name");
        treeNodeConfig.setDeep(4);
        
        return TreeUtil.build(treeNodeList, null, treeNodeConfig, (object, treeNode) -> {
            treeNode.setParentId(object.getParentId());
            treeNode.setId(object.getId());
            treeNode.setName(object.getName());
            treeNode.setWeight(object.getWeight());
            treeNode.putExtra("nodeType", object.getExtra().get("nodeType"));
            treeNode.putExtra("databaseType", object.getExtra().get("databaseType"));
            treeNode.putExtra("enabled", object.getExtra().get("enabled"));
            treeNode.putExtra("activeDimensionName", object.getExtra().get("activeDimensionName"));
        });
    }
}