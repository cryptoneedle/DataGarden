package com.cryptoneedle.garden.infrastructure.vo.ods;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableGenerateResultVo {
    
    @JsonPropertyDescription("建表语句")
    private String createTableSql;
    
    @JsonPropertyDescription("插入语句")
    private String insertIntoSql;
}