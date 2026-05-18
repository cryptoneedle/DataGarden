package com.cryptoneedle.garden.common.enums;

import cn.hutool.v7.crypto.Mode;
import cn.hutool.v7.crypto.Padding;
import cn.hutool.v7.crypto.symmetric.SM4;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*ぃt
 * <p>description: DORIS-表类型 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@AllArgsConstructor
public enum DorisTableType {
    
    BASE_TABLE("表"),
    VIEW("视图"),
    SYSTEM_VIEW("系统视图"),
    ;
    
    private final String description;
    
    public static DorisTableType convert(String tableType) {
        switch (tableType) {
            case "BASE TABLE":
                return BASE_TABLE;
            case "VIEW":
                return VIEW;
            case "SYSTEM VIEW":
                return SYSTEM_VIEW;
            default:
                return null;
        }
    }
}