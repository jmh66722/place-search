package org.example.common.web;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(20))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
    }
}
