package org.example.place.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.common.dto.response.CommonApiResponse;
import org.example.common.dto.response.ErrorResponse;
import org.example.common.dto.response.SingleResponse;
import org.example.place.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "v1")
public class PlaceController {

    private final PlaceService service;

    @Autowired
    public PlaceController(PlaceService service) {
        this.service = service;
    }


    @GetMapping("places")
    @Operation(summary = "장소 조회", description = "키워드로 검색되는 장소 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = CommonApiResponse.class)))}),
            @ApiResponse(responseCode = "500", description = "알 수 없는 오류", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))
    })
    public CommonApiResponse getPlacesByKeyword(
            @RequestParam("q") String keyword
    ) {
        return new SingleResponse(service.getPlacesByKeyword(keyword));
    }
}
