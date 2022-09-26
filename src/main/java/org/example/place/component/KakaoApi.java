package org.example.place.component;

import org.example.place.dto.response.ResponseKakaoLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Component
public class KakaoApi {

    private final RestTemplate restTemplate;

    @Autowired
    public KakaoApi(
            @Value("${kakao.api.host}") String host,
            @Value("${kakao.api.key}") String appKey,
            @Value("${kakao.api.timeout}") Integer timeout
    ) {
        this.restTemplate = new RestTemplateBuilder()
                .setReadTimeout(Duration.ofMillis(timeout))
                .setConnectTimeout(Duration.ofMillis(timeout))
                .defaultHeader("Authorization", appKey)
                .rootUri(host)
                .build();
    }

    public ResponseKakaoLocal getLocalByKeyword(String keyword) {
        String url = UriComponentsBuilder.fromPath("/v2/local/search/keyword.json")
                        .queryParam("query", keyword)
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build(false).toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, ResponseKakaoLocal.class).getBody();
    }
}
