package com.cryptoneedle.garden.entity.doris;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.DorisTableModelType;
import com.cryptoneedle.garden.common.enums.DorisTableType;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>description: DORIS-表-实体 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "doris_table")
@IdClass(DorisTableKey.class)
@Comment("DORIS-表")
public class DorisTable extends BaseEntity {

    @Id
    @Column(length = 64)
    @Comment("数据库")
    private String database;
    @Id
    @Column(length = 64)
    @Comment("表")
    private String table;

    @Column(length = 2048)
    @Comment("说明")
    private String comment;

    @Column(length = 64)
    @Enumerated(EnumType.STRING)
    @Comment("表类型")
    private DorisTableType tableType;
    @Enumerated(EnumType.STRING)
    @Comment("表模型类型")
    private DorisTableModelType tableModelType;

    @Comment("副本数量")
    private Integer replicaCount;
    @Comment("是否分区")
    private Boolean partitioned;
    @Comment("真实桶数量")
    private String bucketNum;
    @Comment("推荐桶数量")
    private Integer estimateBucketNum;
    @Comment("远程存储数据量")
    private String remoteSize;
    @Comment("字段据量")
    private Integer columnNum;
    @Comment("数据量")
    private Long rowNum;
    @Comment("占用空间(格式化)")
    private String storageSpaceFormat;
    @Column(name = "storage_mb", precision = 30, scale = 20)
    @Comment("占用空间(单位：MBytes)")
    private BigDecimal storageMegaBytes;
    @Column(precision = 30, scale = 20)
    @Comment("行平均占用空间(单位：Byte)")
    private BigDecimal avgRowBytes;

    @Column(columnDefinition = "TEXT")
    @Comment("建表语句")
    private String createTableSql;

    @Comment("存储格式")
    private String storageFormat;
    @Comment("倒排索引存储格式")
    private String invertedIndexStorageFormat;
    @Column(length = 64)
    @Comment("表引擎类型")
    private String engine;
    @Column(length = 32)
    @Comment("固定值：utf-8")
    private String collation;
    @Comment("创建时间")
    private LocalDateTime createDt;
    @Comment("更新时间")
    private LocalDateTime updateDt;
}