package org.example.place.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseNaverLocal {
    //Todo: https://developers.naver.com/docs/serviceapi/search/local/local.md#%EC%A7%80%EC%97%AD-%EA%B2%80%EC%83%89-%EA%B2%B0%EA%B3%BC-%EC%A1%B0%ED%9A%8C

    @JsonProperty(value="meta")
    private Meta meta;

    @JsonProperty(value="documents")
    private List<Document> documents;

    @Getter
    @Setter
    @ToString
    public static class Meta{
        @JsonProperty(value="total_count")
        private Integer totalCount;

        @JsonProperty(value="pageable_count")
        private Integer pageableCount;

        @JsonProperty(value="is_end")
        private boolean isEnd;

        @JsonProperty(value="same_name")
        private SameName sameName;
    }

    @Getter
    @Setter
    @ToString
    public static class SameName{
        @JsonProperty(value="region")
        private List<String> region;

        @JsonProperty(value="keyword")
        private String keyword;

        @JsonProperty(value="selected_region")
        private String selectedRegion;
    }

    @Getter
    @Setter
    @ToString
    public static class Document{
        @JsonProperty(value="id")
        private String id;

        @JsonProperty(value="place_name")
        private String place;

        @JsonProperty(value="category_name")
        private String category;

        @JsonProperty(value="category_group_code")
        private String categoryGroupCode;

        @JsonProperty(value="category_group_name")
        private String categoryGroup;

        @JsonProperty(value="phone")
        private String phone;

        @JsonProperty(value="address_name")
        private String address;

        @JsonProperty(value="road_address_name")
        private String roadAddressName;

        @JsonProperty(value="x")
        private String x;

        @JsonProperty(value="y")
        private String y;

        @JsonProperty(value="place_url")
        private String placeUrl;

        @JsonProperty(value="distance")
        private String distance;
    }
}