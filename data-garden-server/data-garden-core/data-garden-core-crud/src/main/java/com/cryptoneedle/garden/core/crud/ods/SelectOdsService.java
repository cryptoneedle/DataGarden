package com.cryptoneedle.garden.core.crud.ods;


import com.cryptoneedle.garden.common.exception.EntityNotFoundException;
import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import com.cryptoneedle.garden.common.key.doris.MappingColumnRelyKey;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumnRely;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTableRely;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingColumnRelyRepository;
import com.cryptoneedle.garden.infrastructure.repository.mapping.MappingTableRelyRepository;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsColumnRepository;
import com.cryptoneedle.garden.infrastructure.repository.ods.OdsTableRepository;
import com.cryptoneedle.garden.infrastructure.vo.ods.OdsColumnVo;
import com.cryptoneedle.garden.infrastructure.vo.ods.OdsTableVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>description: 查询数据应用层(ADS)服务 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-21
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class, transactionManager = "primaryTransactionManager")
public class SelectOdsService {
    
    private final OdsTableRepository odsTableRepository;
    private final OdsColumnRepository odsColumnRepository;
    private final MappingTableRelyRepository mappingTableRelyRepository;
    private final MappingColumnRelyRepository mappingColumnRelyRepository;
    
    public SelectOdsService(OdsTableRepository odsTableRepository,
                            OdsColumnRepository odsColumnRepository,
                            MappingTableRelyRepository mappingTableRelyRepository,
                            MappingColumnRelyRepository mappingColumnRelyRepository) {
        this.odsTableRepository = odsTableRepository;
        this.odsColumnRepository = odsColumnRepository;
        this.mappingTableRelyRepository = mappingTableRelyRepository;
        this.mappingColumnRelyRepository = mappingColumnRelyRepository;
    }
    
    /**
     * OdsTable
     */
    public OdsTable table(DorisTableKey id) {
        return odsTableRepository.findById(id).orElse(null);
    }
    
    public OdsTable table(String tableName) {
        return odsTableRepository.table(tableName);
    }
    
    public OdsTable tableCheck(DorisTableKey id) throws EntityNotFoundException {
        return odsTableRepository.findById(id)
                                 .orElseThrow(() -> new EntityNotFoundException("OdsTable", id.toString()));
    }
    
    public List<OdsTable> tables() {
        return odsTableRepository.tables();
    }
    
    public Page<OdsTableVo> tableVos(Pageable pageable, String tableName) {
        Page<OdsTable> odsTablePage = odsTableRepository.tablesPage(pageable, tableName);
        List<OdsTableVo> voList = odsTablePage.getContent()
                                              .stream()
                                              .map(table -> {
                                                  List<MappingTableRely> mappingTableRelyList =
                                                          mappingTableRelyRepository.findBySource(
                                                                  table.getId().getDatabaseName(),
                                                                  table.getId().getTableName()
                                                          );
                                                  return new OdsTableVo(table, mappingTableRelyList);
                                              })
                                              .collect(Collectors.toList());
        return new PageImpl<>(voList, pageable, odsTablePage.getTotalElements());
    }
    
    public List<OdsColumnVo> columnVos(String tableName) {
        List<OdsColumn> columns = odsColumnRepository.columns(tableName);
        return columns.stream()
                      .map(column -> {
                          MappingColumnRely mappingColumnRely = mappingColumnRelyRepository.getBySource(
                                  column.getId().getDatabaseName(),
                                  column.getId().getTableName(),
                                  column.getId().getColumnName()
                          );
                          return new OdsColumnVo(column, mappingColumnRely);
                      })
                      .collect(Collectors.toList());
    }
    
    public List<OdsTable> tables(String databaseName) {
        return odsTableRepository.tables(databaseName);
    }
    
    /**
     * OdsColumn
     */
    public OdsColumn column(DorisColumnKey id) {
        return odsColumnRepository.findById(id).orElse(null);
    }
    
    public OdsColumn columnCheck(DorisColumnKey id) throws EntityNotFoundException {
        return odsColumnRepository.findById(id)
                                  .orElseThrow(() -> new EntityNotFoundException("OdsColumn", id.toString()));
    }
    
    public List<OdsColumn> columns() {
        return odsColumnRepository.columns();
    }
    
    public List<OdsColumn> columns(String tableName) {
        return odsColumnRepository.columns(tableName);
    }
}