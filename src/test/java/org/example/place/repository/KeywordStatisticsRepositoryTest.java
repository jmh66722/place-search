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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
class KeywordStatisticsRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private KeywordStatisticsRepository repository;

    @ParameterizedTest
    @ValueSource(ints = {1,5,10})
    void findDtoTop_test(int size) {
        System.out.println("count :" +repository.count());

        List<ResponseKeyword> keywords = repository.findDtoByPage(PageRequest.of(0,size));

        Assertions.assertTrue(keywords.size() == size);
    }

    @ParameterizedTest
    @MethodSource("statisticsAggregation_params")
    void statisticsAggregation_test(String keyword, int count) {
        KeywordStatistics before = repository.getById(keyword);

        int expectedCount = before.getTotalCount() + count;
        int c = repository.statisticsAggregation(keyword,count);

        Assertions.assertTrue(c > 0);

        KeywordStatistics after = jdbcTemplate.queryForObject(
                String.format("SELECT * FROM KEYWORD_STATISTICS WHERE KEYWORD = '%s'",keyword),
                (rs, rowNum) ->
                        KeywordStatistics.builder()
                            .keyword(rs.getString("KEYWORD"))
                            .totalCount(rs.getInt("TOTAL_COUNT"))
                            .build()
        );


        Assertions.assertNotNull(after);
        Assertions.assertEquals(expectedCount , after.getTotalCount());

    }
    //statisticsAggregation 테스트 파라미터
    private static Stream<Arguments> statisticsAggregation_params() {
        return Stream.of(
                Arguments.of("맛집",10),
                Arguments.of("카페",20)
        );
    }
}