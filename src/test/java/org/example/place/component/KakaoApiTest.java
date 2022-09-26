package org.example.place.component;

import org.example.place.dto.response.ResponseKakaoLocal;
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
    void getLocalByKeyword() {
        ResponseKakaoLocal result = kakaoApi.getLocalByKeyword("은행");

        List<ResponseKakaoLocal.Document> documents = result.getDocuments();

        Assertions.assertNotNull(documents);
        Assertions.assertTrue(documents.size() <= 10);
    }
}