package com.cryptoneedle.garden.api.task;

import com.cryptoneedle.garden.core.doris.DorisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-29
 */
@Slf4j
@Component
public class SyncDorisTask {
    
    @Autowired
    private DorisService dorisService;
    
    //@Scheduled(fixedRate = 60 * 60 * 1000)
    @Scheduled(cron = "0 30 * * * *")
    public void syncDorisTable() {
        log.info("[SYNC] Doris Catalog");
        dorisService.syncCatalog();
        log.info("[SYNC] Doris Table Statistic");
        dorisService.dorisTableDayIncrement();
    }
}