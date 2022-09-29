package org.example.place.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Component
public class KakaoApi implements LocalOpenApi {

    private final RestTemplate restTemplate;

    private final String format;

    @Autowired
    public KakaoApi(
            @Value("${kakao.api.host}") String host,
            @Value("${kakao.api.app-key}") String appKey,
            @Value("${kakao.api.format}") String format,
            @Value("${kakao.api.timeout}") Integer timeout
    ) {
        this.format = format;
        this.restTemplate = new RestTemplateBuilder()
                .setReadTimeout(Duration.ofMillis(timeout))
                .setConnectTimeout(Duration.ofMillis(timeout))
                .defaultHeader("Authorization", appKey)
                .rootUri(host)
                .build();
    }

    public <T> T getLocalByKeyword(String keyword, Class<T> clazz) {
        return getLocalByKeyword(keyword, null, clazz);
    }
    public <T> T getLocalByKeyword(String keyword, Pageable pageable, Class<T> clazz) {
        String url = UriComponentsBuilder.fromPath(String.format("/v2/local/search/keyword.%s",this.format))
                        .queryParam("query", keyword)
                        .queryParam("page", pageable!=null?pageable.getPageNumber():1)
                        .queryParam("size", pageable!=null?pageable.getPageSize():5)
                        .build(false).toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, clazz).getBody();
    }
}
