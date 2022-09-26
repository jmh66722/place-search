package org.example.place.repository;

import org.example.place.dto.response.ResponseKeyword;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
class KeywordStatisticsRepositoryTest {

    @Autowired
    private KeywordStatisticsRepository repository;

    @ParameterizedTest
    @ValueSource(ints = {1,5,10})
    void findDtoTop(int size) {
        System.out.println("count :" +repository.count());

        List<ResponseKeyword> keywords = repository.findDtoByPage(PageRequest.of(0,size));

        Assertions.assertTrue(keywords.size() == size);
    }
}