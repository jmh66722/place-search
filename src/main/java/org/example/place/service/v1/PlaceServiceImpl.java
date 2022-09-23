package org.example.place.service.v1;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.place.dto.response.ResponseGetKeywordStatistics;
import org.example.place.dto.response.ResponseSearchPlaces;
import org.example.place.repository.KeywordStatisticsRepository;
import org.example.place.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {

    @Autowired
    private KeywordStatisticsRepository repository;

    @SneakyThrows
    public List<ResponseSearchPlaces> getPlacesByKeyword(String keyword) {
        return new ArrayList<>();
    }

    @SneakyThrows
    public List<ResponseGetKeywordStatistics> getKeywordStatistics() {
        return repository.findDtoByLimitNumber(10);
    }

}
