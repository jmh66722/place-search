package org.example.place.component;

import org.example.place.dto.response.ResponseKakaoLocal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = NaverApi.class)
class NaverApiTest {

    @Autowired
    private NaverApi naverApi;

    @Test
    void getLocalByKeyword() {
        ResponseKakaoLocal result = naverApi.getLocalByKeyword("은행");

        List<ResponseKakaoLocal.Document> documents = result.getDocuments();

        Assertions.assertNotNull(documents);
        Assertions.assertTrue(documents.size() <= 10);
    }
}