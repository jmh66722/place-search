package org.example.place.service.v1;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.place.dto.response.ResponseKeywordRank;
import org.example.place.repository.KeywordStatisticsRepository;
import org.example.place.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class KeywordServiceImpl implements KeywordService {

    @Autowired
    private KeywordStatisticsRepository repository;

    @SneakyThrows
    public List<ResponseKeywordRank> getKeywordsRank(int limit) {
        return repository.findDtoTop(limit);
    }

}
