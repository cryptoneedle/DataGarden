package com.cryptoneedle.garden.infrastructure.entity.dolphinScheduler;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <p>description: DolphinScheduler SQL任务实体 </p>
 *
 * @author CryptoNeedle
 * @date 2026-05-26
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
@Table(name = "dolphin_scheduler_task")
@Comment("Dolphin Scheduler 任务")
public class DolphinSchedulerTask {
    
    /**
     * 任务编码（唯一标识）
     */
    @Id
    @Comment("任务编码")
    private String code;
    
    /**
     * 任务类型（SQL）
     */
    @Comment("任务类型")
    private String taskType;
    
    /**
     * 任务参数（JSON格式，包含SQL语句等配置信息）
     */
    @Column(columnDefinition = "TEXT")
    @Comment("Json")
    private String json;
}
