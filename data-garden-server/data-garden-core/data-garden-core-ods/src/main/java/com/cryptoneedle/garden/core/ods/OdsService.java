package com.cryptoneedle.garden.core.ods;

import com.cryptoneedle.garden.common.key.doris.DorisColumnKey;
import com.cryptoneedle.garden.core.crud.*;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumnTranslate;
import com.cryptoneedle.garden.infrastructure.vo.ods.ColumnTranslateResultVo;
import com.cryptoneedle.garden.infrastructure.vo.ods.OdsColumnVo;
import com.cryptoneedle.garden.infrastructure.vo.ods.OdsTableVo;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Service
public class OdsService {
    
    public final OdsService service;
    public final AddService add;
    public final SelectService select;
    public final SaveService save;
    public final DeleteService delete;
    public final PatchService patch;
    
    public OdsService(@Lazy OdsService mappingService,
                          AddService addService,
                          SelectService selectService,
                          SaveService saveService,
                          DeleteService deleteService,
                          PatchService patchService) {
        this.service = mappingService;
        this.add = addService;
        this.select = selectService;
        this.save = saveService;
        this.delete = deleteService;
        this.patch = patchService;
    }
    
    public Page<OdsTableVo> tableVos(Pageable pageable, String tableName) {
        return select.ods.tableVos(pageable, tableName);
    }
    
    public List<OdsColumnVo> columnVos(String tableName) {
        return select.ods.columnVos(tableName);
    }
    
    public List<OdsColumnTranslate> translateColumnList(String tableName, String columnName) {
        return select.ods.columnTranslates(tableName, columnName);
    }
    
    public void saveTranslateColumnList(String tableName,
                                        String columnName,
                                        List<OdsColumnTranslate> odsColumnTranslateList) {
        delete.ods.columnTranslates(tableName, columnName);
        String ods = select.config.dorisSchemaOds();
        OdsColumn column = select.ods.column(new DorisColumnKey(ods, tableName, columnName));
        if (odsColumnTranslateList.isEmpty()) {
            column.setTranslatable(false);
        } else {
            add.ods.columnTranslates(odsColumnTranslateList);
            column.setTranslatable(true);
        }
        save.ods.column(column);
    }
}