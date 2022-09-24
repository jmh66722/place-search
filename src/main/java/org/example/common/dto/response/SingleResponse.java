package org.example.common.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleResponse<T> extends CommonApiResponse {
    private T data;

    public SingleResponse(T data){
        super(true,200);
        this.data = data;
    }
}