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
public class ResponsePlace {

    @Schema(description = "장소명")
    private String title;

    @Schema(description = "주소")
    private String address;
}
