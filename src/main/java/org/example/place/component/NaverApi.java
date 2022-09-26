package org.example.place.component;

import org.example.place.dto.response.ResponseKakaoLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Component
public class NaverApi {

    private final RestTemplate restTemplate;

    @Autowired
    public NaverApi(
            @Value("${naver.api.host}") String host,
            @Value("${naver.api.id}") String id,
            @Value("${naver.api.secret}") String secret,
            @Value("${naver.api.timeout}") Integer timeout
    ) {
        this.restTemplate = new RestTemplateBuilder()
                .setReadTimeout(Duration.ofMillis(timeout))
                .setConnectTimeout(Duration.ofMillis(timeout))
                .defaultHeader("X-Naver-Client-Id", id)
                .defaultHeader("X-Naver-Client-Secret", secret)
                .rootUri(host)
                .build();
    }

    public ResponseKakaoLocal getLocalByKeyword(String keyword) {
        String url = UriComponentsBuilder.fromPath("/v1/search/local.json")
                        .queryParam("query", keyword)
                        .queryParam("display", 10)
                        .build(false).toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, ResponseKakaoLocal.class).getBody();
    }
}
