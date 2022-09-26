package org.example.place.repository;

import org.example.place.dto.response.ResponseKeyword;
import org.example.place.entity.KeywordStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordStatisticsRepository extends JpaRepository<KeywordStatistics,String> {

    @Query(value = "select " +
            "new org.example.place.dto.response.ResponseKeyword(e.keyword, e.totalCount) " +
            "from KEYWORD_STATISTICS e " +
            "order by e.totalCount desc")
    List<ResponseKeyword> findDtoByPage(Pageable pageable);

}
