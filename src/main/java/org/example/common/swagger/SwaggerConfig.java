package org.example.common.swagger;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Value("${app.version}")
    private String appVersion;

    @Bean
    public OpenApiCustomiser openAPI(){

        return OpenAPI -> OpenAPI
                //swagger 기본 문서 설정
                .info(new Info().title("Place-API"+" Docs").version(appVersion)
                        .description("장소 검색 API 문서입니다.")
                        .contact(
                            new Contact()
                                .name("정명한")
                                .email("jmh667722@gmail.com")
                        )
                )
                ;
    }

}
