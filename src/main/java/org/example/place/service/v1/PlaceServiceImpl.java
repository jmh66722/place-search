package org.example.place.service.v1;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.place.dto.response.ResponsePlace;
import org.example.place.repository.SearchPlaceRepository;
import org.example.place.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {

    @Autowired
    private SearchPlaceRepository searchPlaceRepository;

    @SneakyThrows
    public List<ResponsePlace> getPlacesByKeyword(String keyword) {
        searchPlaceRepository.increaseCount(keyword);

        return new ArrayList<>();
    }

}
