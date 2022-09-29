package org.example.place.component;

import org.example.place.dto.response.ResponsePlaceOpenApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest(classes = NaverApi.class)
class NaverApiTest {

    @Autowired
    private NaverApi naverApi;

    @Test
    void getLocalByKeyword_test() {
        Map result1 = naverApi.getLocalByKeyword("은행", HashMap.class);
        ResponsePlaceOpenApi result = naverApi.getLocalByKeyword("은행", ResponsePlaceOpenApi.class);
        Assertions.assertNotNull(result);

        List<ResponsePlaceOpenApi.Document> documents = result.getDocuments();
        Assertions.assertNotNull(documents);
        Assertions.assertTrue(documents.size() <= 10);
    }
}