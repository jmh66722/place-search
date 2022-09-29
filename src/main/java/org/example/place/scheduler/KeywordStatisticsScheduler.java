package org.example.place.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.example.place.repository.KeywordStatisticsRepository;
import org.example.place.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeywordStatisticsScheduler {

    private final SearchHistoryRepository searchHistoryRepository;
    private final KeywordStatisticsRepository keywordStatisticsRepository;

    @Autowired
    public KeywordStatisticsScheduler(SearchHistoryRepository searchHistoryRepository, KeywordStatisticsRepository keywordStatisticsRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.keywordStatisticsRepository = keywordStatisticsRepository;
    }

    @Scheduled(cron = "*/10 * * * *")
    @SchedulerLock(name = "keywordStatisticsCounting" , lockAtLeastFor = "10s", lockAtMostFor = "20s")
    public void keywordStatisticsAggregation(){
//        searchHistoryRepository.

//        keywordStatisticsRepository.statisticsAggregation();
    }
}
