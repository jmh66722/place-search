package org.example.place.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.common.error.ErrorResult;
import org.example.place.dto.response.ResponseGetKeywordStatistics;
import org.example.place.dto.response.ResponseSearchPlaces;
import org.example.place.service.v1.PlaceServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "v1")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceServiceImpl service;


    @GetMapping("places")
    @Operation(summary = "장소 조회", description = "키워드로 검색되는 장소 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = List.class)))}),
            @ApiResponse(responseCode = "500", description = "알 수 없는 오류", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResult.class))))
    })
    public List<ResponseSearchPlaces> getPlacesByKeyword(
            @RequestParam("p") String keyword
    ) {
        return service.getPlacesByKeyword(keyword);
    }


    @GetMapping("keyword/statistic")
    @Operation(summary = "키워드 통계 조회", description = "가장 많이 검색되는 키워드로 10개 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = List.class)))}),
            @ApiResponse(responseCode = "500", description = "알 수 없는 오류", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResult.class))))
    })
    public List<ResponseGetKeywordStatistics> getKeywordStatics(
    ) {
        return service.getKeywordStatistics();
    }
}
