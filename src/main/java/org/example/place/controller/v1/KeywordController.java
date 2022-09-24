package org.example.place.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.response.CommonApiResponse;
import org.example.common.dto.response.ErrorResponse;
import org.example.common.dto.response.ListResponse;
import org.example.place.service.v1.PlaceServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "v1")
@RequiredArgsConstructor
public class KeywordController {

    private final PlaceServiceImpl service;


    @GetMapping("keywords/rank")
    @Operation(summary = "키워드 목록 조회", description = "가장 많이 검색되는 키워드로 10개 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = CommonApiResponse.class)))}),
            @ApiResponse(responseCode = "500", description = "알 수 없는 오류", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    })
    public CommonApiResponse getKeywordsRank(
    ) {
        return new ListResponse(service.getKeywordsRank(10));
    }
}
