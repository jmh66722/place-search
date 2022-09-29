package org.example.place.service.v1;

import org.example.place.dto.response.ResponseKeyword;
import org.example.place.repository.KeywordStatisticsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@DataJpaTest
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
class KeywordServiceImplTest {

    @Autowired
    private KeywordStatisticsRepository keywordStatisticsRepository;
    private KeywordServiceImpl service;

    @BeforeEach
    public void setUp(){
        this.service = new KeywordServiceImpl(keywordStatisticsRepository);
    }

    @Test
    void getPopularKeywords_test() {
        List<ResponseKeyword> result = service.getPopularKeywords();

        Assertions.assertTrue(result.size() > 0);
        Assertions.assertTrue(result.size() <= 10);

        AtomicInteger postValue = new AtomicInteger(Integer.MAX_VALUE);
        result.forEach(k -> {
            Assertions.assertTrue(k.getTotalCount() <= postValue.get());
            postValue.set(k.getTotalCount());
        });
    }
}