package org.example.place.service.v1;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.place.dto.response.ResponsePlace;
import org.example.place.entity.SearchHistory;
import org.example.place.repository.KeywordStatisticsRepository;
import org.example.place.repository.SearchHistoryRepository;
import org.example.place.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {

    private final KeywordStatisticsRepository keywordStatisticsRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @Autowired
    public PlaceServiceImpl(KeywordStatisticsRepository keywordStatisticsRepository, SearchHistoryRepository searchHistoryRepository) {
        this.keywordStatisticsRepository = keywordStatisticsRepository;
        this.searchHistoryRepository = searchHistoryRepository;
    }

    @SneakyThrows
    @Transactional
    public List<ResponsePlace> getPlacesByKeyword(String keyword) {
        String url = "search/keyword.json";

        searchHistoryRepository.save(SearchHistory
                .builder()
                .keyword(keyword)
                .result("")
                .build());

        return new ArrayList<>();
    }

}
