package org.example.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Schema
public class ResponseKeyword {

    @Schema(description = "키워드")
    private String keyword;

    @Schema(description = "키워드 검색 횟수")
    private Integer totalCount;
}
