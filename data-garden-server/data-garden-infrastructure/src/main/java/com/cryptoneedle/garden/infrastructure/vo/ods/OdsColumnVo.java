package com.cryptoneedle.garden.infrastructure.vo.ods;

import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingColumnRely;
import com.cryptoneedle.garden.infrastructure.entity.mapping.MappingTableRely;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsColumn;
import com.cryptoneedle.garden.infrastructure.entity.ods.OdsTable;
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
public class OdsColumnVo {
    
    private OdsColumn odsColumn;
    private MappingColumnRely mappingColumnRely;
}