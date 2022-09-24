package org.example.place.repository;

import org.example.place.dto.response.ResponseKeywordRank;
import org.example.place.entity.KeywordStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordStatisticsRepository extends JpaRepository<KeywordStatistics,String> {

    @Query("select " +
            "new org.example.place.dto.response.ResponseGetKeywordStatics(e.keyword, e.total_count) " +
            "from keyword_statistics e " +
            "order by e.search_count desc " +
            "limit :limit")
    List<ResponseKeywordRank> findDtoTop(@Param("limit") int limit);

}
