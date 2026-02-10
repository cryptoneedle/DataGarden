package com.cryptoneedle.garden.infrastructure.vo.dwd;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class LimsAssetCatalogImportVo {
    @ExcelProperty(index = 0)
    private String seq;

    @ExcelProperty(index = 1)
    private String businessObjectName;

    @ExcelProperty(index = 2)
    private String businessObjectCode;

    @ExcelProperty(index = 3)
    private String logicalEntityName;

    @ExcelProperty(index = 4)
    private String logicalEntityCode;

    @ExcelProperty(index = 5)
    private String tableDescription;

    @ExcelProperty(index = 6)
    private String issue;

    @ExcelProperty(index = 7)
    private String fieldName;

    @ExcelProperty(index = 8)
    private String fieldCode;

    @ExcelProperty(index = 9)
    private String originalFieldName;

    @ExcelProperty(index = 10)
    private String description;
}