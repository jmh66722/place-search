package org.example.place.service.v1;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.place.dto.response.ResponseKeyword;
import org.example.place.repository.KeywordStatisticsRepository;
import org.example.place.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
public class KeywordServiceImpl implements KeywordService {

    private final KeywordStatisticsRepository repository;

    @Autowired
    public KeywordServiceImpl(KeywordStatisticsRepository repository) {
        this.repository = repository;
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public List<ResponseKeyword> getPopularKeywords() {
        return repository.findDtoByPage(PageRequest.of(0,10));
    }

}
