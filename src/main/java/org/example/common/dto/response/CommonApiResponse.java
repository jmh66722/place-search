package org.example.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema
public class CommonApiResponse {

    @Schema(description = "응답 성공 결과 : true/false")
    protected boolean result;
    
    @Schema(description = "http 응답 코드")
    protected int status;

    public CommonApiResponse(boolean result, int status){
        this.result = result;
        this.status = status;
    }
}
