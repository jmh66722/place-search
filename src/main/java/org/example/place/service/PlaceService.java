package org.example.place.service;

import org.example.place.dto.response.ResponsePlace;

import java.util.List;


public interface PlaceService {

    /**
    * 장소 검색 후 목록 반환
    * @param {String} keyword
    * @return {@link List< ResponsePlace >}
    * */
    List<ResponsePlace> getPlacesByKeyword(String keyword);
}
