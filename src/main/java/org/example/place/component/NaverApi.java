package org.example.place.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Component
public class NaverApi implements LocalOpenApi {

    private final RestTemplate restTemplate;

    private final String format;

    @Autowired
    public NaverApi(
            @Value("${naver.api.host}") String host,
            @Value("${naver.api.client-id}") String id,
            @Value("${naver.api.client-secret}") String secret,
            @Value("${naver.api.format}") String format,
            @Value("${naver.api.timeout}") Integer timeout
    ) {
        this.format = format;
        this.restTemplate = new RestTemplateBuilder()
                .setReadTimeout(Duration.ofMillis(timeout))
                .setConnectTimeout(Duration.ofMillis(timeout))
                .defaultHeader("X-Naver-Client-Id", id)
                .defaultHeader("X-Naver-Client-Secret", secret)
                .rootUri(host)
                .build();
    }


    public <T> T getLocalByKeyword(String keyword, Class<T> clazz) {
        return getLocalByKeyword(keyword, null, clazz);
    }
    public <T> T getLocalByKeyword(String keyword, Pageable pageable, Class<T> clazz) {
        String url = UriComponentsBuilder.fromPath(String.format("/v1/search/local.%s",this.format))
                        .queryParam("query", keyword)
                        .queryParam("start", pageable!=null?pageable.getPageNumber():1)
                        .queryParam("display", pageable!=null?pageable.getPageSize():5)
                        .build(false).toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, clazz).getBody();
    }
}
