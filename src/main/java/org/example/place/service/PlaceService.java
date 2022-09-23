package org.example.place.service;

import org.example.place.dto.response.ResponseGetKeywordStatistics;
import org.example.place.dto.response.ResponseSearchPlaces;

import java.util.List;


public interface PlaceService {


    /**
    * 장소 검색 후 목록 반환
    * @param {String} keyword
    * @return {@link List<ResponseSearchPlaces>}
    * */
    List<ResponseSearchPlaces> getPlacesByKeyword(String keyword);


    /**
    * 키워드별 검색 통계 조회
    * @return {@link List< ResponseGetKeywordStatistics>}
    * */
    List<ResponseGetKeywordStatistics> getKeywordStatistics();
}
