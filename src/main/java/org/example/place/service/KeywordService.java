package org.example.place.service;

import org.example.place.dto.response.ResponseKeywordRank;

import java.util.List;


public interface KeywordService {

    /**
    * 검색 순위가 높은 키워드 조회
    * @param {int} limit
    * @return {@link List< ResponseKeywordRank >}
    * */
    List<ResponseKeywordRank> getKeywordsRank(int limit);
}
