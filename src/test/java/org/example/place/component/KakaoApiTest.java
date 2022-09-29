package org.example.place.component;

import org.example.place.dto.response.ResponsePlaceOpenApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest(classes = KakaoApi.class)
class KakaoApiTest {

    @Autowired
    private KakaoApi kakaoApi;

    @Test
    void getLocalByKeyword_test() {
        ResponsePlaceOpenApi result = kakaoApi.getLocalByKeyword("은행", ResponsePlaceOpenApi.class);
        Assertions.assertNotNull(result);

        List<ResponsePlaceOpenApi.Document> documents = result.getDocuments();
        Assertions.assertNotNull(documents);
        Assertions.assertTrue(documents.size() <= 10);
    }
}