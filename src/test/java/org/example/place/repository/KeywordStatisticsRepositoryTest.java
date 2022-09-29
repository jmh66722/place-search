package org.example.place.repository;

import org.example.place.dto.response.ResponseKeyword;
import org.example.place.entity.KeywordStatistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
class KeywordStatisticsRepositoryTest {

    @Autowired
    private KeywordStatisticsRepository repository;

    @ParameterizedTest
    @ValueSource(ints = {1,5,10})
    void findDtoTopTest(int size) {
        System.out.println("count :" +repository.count());

        List<ResponseKeyword> keywords = repository.findDtoByPage(PageRequest.of(0,size));

        Assertions.assertTrue(keywords.size() == size);
    }

    @ParameterizedTest
    @MethodSource("saveParams")
    void saveTest(String keyword, int count) {
        KeywordStatistics before = repository.getById(keyword);

        int newCount = before.getTotalCount() + count;
        repository.save(KeywordStatistics.builder()
                .keyword(keyword)
                .totalCount(newCount)
                .build());

        KeywordStatistics after = repository.getById(keyword);

        Assertions.assertNotNull(after);
        Assertions.assertEquals(newCount , after.getTotalCount());

    }
    //save 테스트 파라미터
    private static Stream<Arguments> saveParams() {
        return Stream.of(
                Arguments.of("맛집",10),
                Arguments.of("카페",20)
        );
    }
}