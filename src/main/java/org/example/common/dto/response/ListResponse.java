package org.example.common.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResponse<T> extends CommonApiResponse {
    private List<T> data;

    public ListResponse(List<T> data) {
        super(true,200);
        this.data = data;
    }
}