package org.example.place.repository;

import org.example.place.entity.SearchHistory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.stream.Stream;

@DataJpaTest
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
class SearchHistoryRepositoryTest {

    @Autowired
    private SearchHistoryRepository repository;


    @ParameterizedTest
    @MethodSource("save_params")
    void save_test(SearchHistory history) {
        SearchHistory result = repository.save(history);

        Assertions.assertNotNull(result);
    }
    //save 테스트 파라미터
    private static Stream<Arguments> save_params() {
        return Stream.of(
                // 모두 있는 경우
                Arguments.of(new SearchHistory().builder()
                        .keyword("test")
                        .result("")
                        .build())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"은행", "맛집"})
    void findTopByKeyword_test(String keyword) {
        Optional<SearchHistory> result = repository.findTopByKeywordOrderByIdDesc(keyword);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNotNull(result.get());
    }
}