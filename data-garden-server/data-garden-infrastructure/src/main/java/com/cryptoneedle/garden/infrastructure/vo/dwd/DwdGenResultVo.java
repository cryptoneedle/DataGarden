package com.cryptoneedle.garden.infrastructure.vo.dwd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-05-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DwdGenResultVo {
    
    private String createTableSql;
    private String insertTableSql;
}