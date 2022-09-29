package org.example.place.service;

import org.example.place.dto.response.ResponseKeyword;

import java.util.List;


public interface KeywordService {

    /**
    * 검색 순위가 높은 키워드 조회
    * @param {int} limit
    * @return {@link List<    ResponseKeyword    >}
    * */
    List<ResponseKeyword> getPopularKeywords();
}
