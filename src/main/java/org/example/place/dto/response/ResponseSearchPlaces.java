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
public class ResponseSearchPlaces {

    @Schema(description = "장소명")
    private String title;
}
