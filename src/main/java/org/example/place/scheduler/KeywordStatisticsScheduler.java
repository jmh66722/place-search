package org.example.place.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.example.place.entity.KeywordStatistics;
import org.example.place.repository.KeywordStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KeywordStatisticsScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final KeywordStatisticsRepository keywordStatisticsRepository;

    @Autowired
    public KeywordStatisticsScheduler(JdbcTemplate jdbcTemplate, KeywordStatisticsRepository keywordStatisticsRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.keywordStatisticsRepository = keywordStatisticsRepository;
    }

    @Scheduled(cron = "${scheduler.statistics.cron}")
//    @SchedulerLock(name = "keywordStatisticsCounting" , lockAtLeastFor = "10s", lockAtMostFor = "20s")
    public void keywordStatisticsAggregation(){
        log.info("Start Statistics Aggregation");

        // keyword 로 기록된 값을 가져온다.
        List<KeywordStatistics> list = jdbcTemplate.query(
                "SELECT KEYWORD, COUNT(*) AS TOTAL_COUNT FROM SEARCH_HISTORY GROUP BY KEYWORD",
                (rs, rowNum) -> KeywordStatistics.builder()
                        .keyword(rs.getString("KEYWORD"))
                        .totalCount(rs.getInt("TOTAL_COUNT"))
                        .build()
        );

        //집계된 값을 저장
        keywordStatisticsRepository.saveAll(list);

    }
}
