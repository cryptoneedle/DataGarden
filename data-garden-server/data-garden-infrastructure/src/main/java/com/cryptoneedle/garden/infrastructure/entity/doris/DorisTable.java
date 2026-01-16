package com.cryptoneedle.garden.infrastructure.entity.doris;

import com.bubbles.engine.data.core.entity.BaseEntity;
import com.cryptoneedle.garden.common.enums.DorisTableModelType;
import com.cryptoneedle.garden.common.enums.DorisTableType;
import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Builder
@Accessors(chain = true)
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "doris_table")
@Comment("DORIS-表")
public class DorisTable extends BaseEntity {
    
    @EmbeddedId
    private DorisTableKey id;
    
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
    private Boolean partitioned = false;
    @Comment("真实桶数量")
    private String bucketNum;
    @Comment("推荐桶数量")
    private Integer estimateBucketNum;
    @Comment("远程存储数据量")
    private String remoteSize;
    @Comment("字段数量")
    private Integer columnNum;
    @Comment("数据量")
    private Long rowNum;
    @Comment("占用空间(格式化)")
    private String storageSpaceFormat;
    @Column(name = "storage_bytes")
    @Comment("占用空间(单位：Bytes)")
    private Long storageBytes;
    @Comment("行平均占用空间(单位：Byte)")
    private Long avgRowBytes;
    
    @Comment("创建时间")
    private LocalDateTime createDt;
    @Comment("更新时间")
    private LocalDateTime updateDt;
    
    @Column(columnDefinition = "TEXT")
    @Comment("建表语句")
    private String createTableScript;
}