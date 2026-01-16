package com.cryptoneedle.garden.infrastructure.entity.doris;

import com.cryptoneedle.garden.common.key.doris.DorisTableKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

/**
 * <p>description: Doris-元数据-TableStatus </p>
 * <p>
 * 数据来源：SHOW TABLE STATUS FROM internal.xxx;
 *
 * @author CryptoNeedle
 * @date 2025-09-25
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
@Table(name = "doris_table_script")
@Comment("DORIS-表")
public class DorisShowCreateTable implements Serializable {
    
    @EmbeddedId
    private DorisTableKey id;

    @Comment("建表语句")
    private String createTableScript;
}