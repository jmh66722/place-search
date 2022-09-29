package org.example.place.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ResponsePlaceOpenApi {

    @JsonAlias({"total_count","total"})
    private Integer totalCount;

    @JsonAlias({"pageable_count","start"})
    private Integer pageableCount;

    @JsonAlias({"documents","items"})
    private List<Document> documents;

    @Getter
    @Setter
    @Builder
    @ToString
    public static class Document{
        @JsonAlias({"place_name","title"})
        private String title;

        @JsonAlias({"category_name","category"})
        private String category;

        @JsonAlias({"phone","telephone"})
        private String phone;

        @JsonAlias({"address_name","address"})
        private String address;

        @JsonAlias({"road_address_name","roadAddress"})
        private String roadAddress;

        @JsonAlias({"x","mapx"})
        private String x;

        @JsonAlias({"y","mapy"})
        private String y;

        @JsonAlias({"place_url","link"})
        private String placeUrl;
    }

    @JsonProperty("meta")
    private void unpackFromNestedObject(Map<String, Object> meta) {
        this.totalCount = Integer.parseInt(meta.get("total_count").toString());
        this.pageableCount = Integer.parseInt(meta.get("pageable_count").toString());
    }
}