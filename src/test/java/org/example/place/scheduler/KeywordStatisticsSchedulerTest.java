package org.example.place.scheduler;

import org.example.place.entity.KeywordStatistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
@SpringBootTest
class KeywordStatisticsSchedulerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private KeywordStatisticsScheduler statisticsScheduler;

    @Test
    void keywordStatisticsAggregation_test() {
        List<KeywordStatistics> before = jdbcTemplate.query(
                        String.format("SELECT * FROM KEYWORD_STATISTICS"),
                        (rs, rowNum) ->
                                KeywordStatistics.builder()
                                    .keyword(rs.getString("KEYWORD"))
                                    .totalCount(rs.getInt("TOTAL_COUNT"))
                                    .build()
                );

        statisticsScheduler.keywordStatisticsAggregation();

        List<KeywordStatistics> after = jdbcTemplate.query(
                String.format("SELECT * FROM KEYWORD_STATISTICS"),
                (rs, rowNum) ->
                        KeywordStatistics.builder()
                            .keyword(rs.getString("KEYWORD"))
                            .totalCount(rs.getInt("TOTAL_COUNT"))
                            .build()
        );

        Assertions.assertEquals(before.get(0).getKeyword(), after.get(0).getKeyword());
        Assertions.assertNotEquals(before.get(0).getTotalCount(), after.get(0).getTotalCount());
    }
}