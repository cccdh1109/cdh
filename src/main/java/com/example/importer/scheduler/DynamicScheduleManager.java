package com.example.importer.scheduler;

import com.example.importer.domain.ScheduleConfig;
import com.example.importer.mapper.ScheduleConfigMapper;
import com.example.importer.service.ImportOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class DynamicScheduleManager {

    private static final Logger log = LoggerFactory.getLogger(DynamicScheduleManager.class);

    private final ScheduleConfigMapper scheduleConfigMapper;
    private final ImportOrchestrator importOrchestrator;
    private final ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> futures = new HashMap<>();

    @Value("${dynamic.schedule.reload-interval-ms:60000}")
    private long reloadIntervalMs;

    public DynamicScheduleManager(ScheduleConfigMapper scheduleConfigMapper,
                                  ImportOrchestrator importOrchestrator,
                                  ThreadPoolTaskScheduler scheduler) {
        this.scheduleConfigMapper = scheduleConfigMapper;
        this.importOrchestrator = importOrchestrator;
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void init() {
        scheduler.initialize();
        refreshSchedules();
        scheduler.scheduleWithFixedDelay(this::safeRefresh, reloadIntervalMs);
        log.info("[SCHEDULE][INIT] dynamic schedule manager started, reloadIntervalMs={}", reloadIntervalMs);
    }

    private void safeRefresh() {
        try {
            refreshSchedules();
        } catch (Exception ex) {
            log.error("[SCHEDULE][ERROR] failed to refresh schedules, message={}", ex.getMessage(), ex);
        }
    }

    public synchronized void refreshSchedules() {
        List<ScheduleConfig> configs = scheduleConfigMapper.selectEnabledSchedules();
        Map<String, ScheduleConfig> latest = new HashMap<>();
        for (ScheduleConfig config : configs) {
            latest.put(config.getTaskCode(), config);
        }

        for (String taskCode : new HashMap<>(futures).keySet()) {
            if (!latest.containsKey(taskCode)) {
                ScheduledFuture<?> future = futures.remove(taskCode);
                if (future != null) {
                    future.cancel(false);
                }
                log.info("[SCHEDULE][REMOVE] taskCode={}", taskCode);
            }
        }

        for (ScheduleConfig config : configs) {
            ScheduledFuture<?> existed = futures.get(config.getTaskCode());
            if (existed != null) {
                existed.cancel(false);
            }
            Trigger trigger = new CronTrigger(config.getCronExpression());
            ScheduledFuture<?> future = scheduler.schedule(() -> runTask(config), trigger);
            futures.put(config.getTaskCode(), future);
            log.info("[SCHEDULE][UPSERT] taskCode={}, cron={}, fileConfigId={}",
                    config.getTaskCode(), config.getCronExpression(), config.getFileConfigId());
        }
    }

    private void runTask(ScheduleConfig config) {
        log.info("[SCHEDULE][TRIGGER] taskCode={}, fileConfigId={}", config.getTaskCode(), config.getFileConfigId());
        importOrchestrator.executeImport(config.getFileConfigId());
    }
}
