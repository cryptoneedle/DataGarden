package com.cryptoneedle.garden.infrastructure.repository.doris;

import com.bubbles.engine.data.core.repository.BaseRepository;
import com.cryptoneedle.garden.common.key.doris.DorisTableStatisticKey;
import com.cryptoneedle.garden.infrastructure.entity.doris.DorisTableRowNum;
import org.springframework.stereotype.Repository;

/**
 * <p>description: DORIS-表统计-存储 </p>
 *
 * @author CryptoNeedle
 * @date 2025-11-20
 */
@Repository
public interface DorisTableRowNumRepository extends BaseRepository<DorisTableRowNum, DorisTableStatisticKey> {
    

}