package com.cryptoneedle.garden.infrastructure.dto;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>description: Doris-元数据-Catalog </p>
 * <p>
 * 数据来源： SHOW CATALOGS;
 *
 * @author CryptoNeedle
 * @date 2025-09-25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class DorisExecShowCatalogs {

    // 数据目录唯一 ID
    private Long catalogId;

    // 数据目录名称，其中 internal 是默认内置的 catalog，不可修改
    private String catalogName;

    // 数据目录类型
    private String type;

    // 是否为当前正在使用的数据目录
    private String isCurrent;

    // 创建时间
    private String createTime;

    // 最后更新时间
    private String lastUpdateTime;

    // 备注
    private String comment;
}