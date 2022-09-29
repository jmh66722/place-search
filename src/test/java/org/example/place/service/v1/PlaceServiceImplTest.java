package org.example.place.service.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.place.component.KakaoApi;
import org.example.place.component.LocalOpenApi;
import org.example.place.component.NaverApi;
import org.example.place.dto.response.ResponsePlace;
import org.example.place.dto.response.ResponsePlaceOpenApi;
import org.example.place.entity.SearchHistory;
import org.example.place.repository.SearchHistoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@Transactional
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
@SpringBootTest
class PlaceServiceImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlaceServiceImpl service;

    @MockBean
    private KakaoApi kakaoApi;

    @MockBean
    private NaverApi naverApi;

    @MockBean
    private SearchHistoryRepository searchHistoryRepository;

    private ObjectMapper mapper = new ObjectMapper();



    @ParameterizedTest
    @MethodSource("mergeDocuments_params")
    void mergeDocuments_test(List<ResponsePlaceOpenApi.Document> kakaoDocs, List<ResponsePlaceOpenApi.Document> naverDocs, List<ResponsePlace> expected) throws JsonProcessingException {
        List<ResponsePlace> result = ReflectionTestUtils.invokeMethod(service,"mergeDocuments",10, new List[]{kakaoDocs, naverDocs});

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mapper.writeValueAsString(expected), mapper.writeValueAsString(result));
    }

    @ParameterizedTest
    @MethodSource("mergeDocuments_params")
    void getPlacesByKeywordFromApis_test(List<ResponsePlaceOpenApi.Document> kakaoDocs, List<ResponsePlaceOpenApi.Document> naverDocs, List<ResponsePlace> expected) throws JsonProcessingException {
        //mockito when
        when(kakaoApi.getLocalByKeyword("", PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(kakaoDocs).build());
        when(kakaoApi.getLocalByKeyword("", PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(kakaoDocs).build());
        when(naverApi.getLocalByKeyword("", PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(naverDocs).build());
        when(naverApi.getLocalByKeyword("", PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(naverDocs).build());

        LocalOpenApi[] apis = {kakaoApi,naverApi};
        List<ResponsePlace> result = ReflectionTestUtils.invokeMethod(service,"getPlacesByKeywordFromApis","", apis);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mapper.writeValueAsString(expected), mapper.writeValueAsString(result));
    }

    @ParameterizedTest
    @MethodSource("mergeDocuments_params")
    void getPlacesByKeyword_test(List<ResponsePlaceOpenApi.Document> kakaoDocs, List<ResponsePlaceOpenApi.Document> naverDocs, List<ResponsePlace> expected) throws JsonProcessingException {
        //mockito when
        when(kakaoApi.getLocalByKeyword("", PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(kakaoDocs).build());
        when(kakaoApi.getLocalByKeyword("", PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(kakaoDocs).build());
        when(naverApi.getLocalByKeyword("", PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(naverDocs).build());
        when(naverApi.getLocalByKeyword("", PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(naverDocs).build());

        List<ResponsePlace> result = service.getPlacesByKeyword("");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mapper.writeValueAsString(expected), mapper.writeValueAsString(result));

        //이력이 저장되었는지 확인
        SearchHistory history = jdbcTemplate.queryForObject("SELECT * FROM SEARCH_HISTORY WHERE KEYWORD = ''", (rs, rowNum) ->
            SearchHistory.builder()
                    .id(rs.getInt("ID"))
                    .keyword(rs.getString("KEYWORD"))
                    .result(rs.getString("RESULT"))
                    .build()
        );

        Assertions.assertNotNull(history);
        Assertions.assertNotNull(mapper.writeValueAsString(result), history.getResult());
    }

    @ParameterizedTest
    @ValueSource(strings = {"맛집","카페"})
    void getPlacesByKeyword_failureCallApis_test(String keyword) {
        //mockito when
        when(kakaoApi.getLocalByKeyword(keyword, PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        when(kakaoApi.getLocalByKeyword(keyword, PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        when(naverApi.getLocalByKeyword(keyword, PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        when(naverApi.getLocalByKeyword(keyword, PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        when(searchHistoryRepository.findTopByKeywordOrderByIdDesc(keyword))
                .thenReturn(Optional.of(SearchHistory.builder().result("[{}]").build()));

        List<ResponsePlace> result = service.getPlacesByKeyword(keyword);

        //then
        Assertions.assertNotNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"맛집","카페","fail"})
    void getPlacesByKeyword_failureQuery_test(String keyword) {
        //mockito when
        when(kakaoApi.getLocalByKeyword(keyword, PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        when(kakaoApi.getLocalByKeyword(keyword, PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        when(naverApi.getLocalByKeyword(keyword, PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        when(naverApi.getLocalByKeyword(keyword, PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        when(searchHistoryRepository.findTopByKeywordOrderByIdDesc(keyword))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        //then
        Assertions.assertThrows(HttpClientErrorException.class, () -> service.getPlacesByKeyword(keyword));
    }

    @ParameterizedTest
    @MethodSource("mergeDocuments_params")
    void 동시성_테스트(List<ResponsePlaceOpenApi.Document> kakaoDocs, List<ResponsePlaceOpenApi.Document> naverDocs, List<ResponsePlace> expected) throws InterruptedException {
        //mockito when
        when(kakaoApi.getLocalByKeyword("", PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(kakaoDocs).build());
        when(kakaoApi.getLocalByKeyword("", PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(kakaoDocs).build());
        when(naverApi.getLocalByKeyword("", PageRequest.of(1,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(naverDocs).build());
        when(naverApi.getLocalByKeyword("", PageRequest.of(2,5), ResponsePlaceOpenApi.class))
                .thenReturn(ResponsePlaceOpenApi.builder().documents(naverDocs).build());

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);


        List<ResponsePlace> result1 = new ArrayList<>();
        List<ResponsePlace> result2 = new ArrayList<>();

        executorService.execute(()-> {
            result1.addAll(service.getPlacesByKeyword("1"));
            latch.countDown();
        });
        executorService.execute(()-> {
            result2.addAll(service.getPlacesByKeyword("1"));
            latch.countDown();
        });

        latch.await();

        List<SearchHistory> history = jdbcTemplate.query("SELECT * FROM SEARCH_HISTORY WHERE KEYWORD = '1'", (rs, rowNum) ->
            SearchHistory.builder()
                    .id(rs.getInt("ID"))
                    .keyword(rs.getString("KEYWORD"))
                    .result(rs.getString("RESULT"))
                    .build()
        );

        Assertions.assertNotNull(history);

    }
    //mergeDocuments 테스트 파라미터
    private static Stream<Arguments> mergeDocuments_params() {
        return Stream.of(
                // 중복된 은행 2개가 우선, 입력된 arguments 순서대로 우선순위가 맞는지 확인
                Arguments.of(
                        new ArrayList<ResponsePlaceOpenApi.Document>(){{
                            add(ResponsePlaceOpenApi.Document.builder().title("NH농협은행").address("1-1").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("우리은행").address("1-2").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("국민은행").address("1").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("부산은행").address("1-3").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("신한은행").address("2").build());
                        }},
                        new ArrayList<ResponsePlaceOpenApi.Document>(){{
                            add(ResponsePlaceOpenApi.Document.builder().title("SBI저축은행").address("2-1").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("국민은행").address("1").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("신한은행").address("2").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("케이뱅크").address("2-2").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("대구은행").address("2-3").build());
                        }},
                        //expected
                        new ArrayList<ResponsePlace>(){{
                            add(ResponsePlace.builder().title("국민은행").address("1").build());
                            add(ResponsePlace.builder().title("신한은행").address("2").build());
                            add(ResponsePlace.builder().title("NH농협은행").address("1-1").build());
                            add(ResponsePlace.builder().title("우리은행").address("1-2").build());
                            add(ResponsePlace.builder().title("부산은행").address("1-3").build());
                            add(ResponsePlace.builder().title("SBI저축은행").address("2-1").build());
                            add(ResponsePlace.builder().title("케이뱅크").address("2-2").build());
                            add(ResponsePlace.builder().title("대구은행").address("2-3").build());
                        }}
                ),
                // 중복된 결과가 없는 경우 우선순위먼저
                Arguments.of(
                        new ArrayList<ResponsePlaceOpenApi.Document>(){{
                            add(ResponsePlaceOpenApi.Document.builder().title("신한은행").address("1-1").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("국민은행").address("1-2").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("NH농협은행").address("1-3").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("우리은행").address("1-4").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("KB국민은행").address("1-5").build());
                        }},
                        new ArrayList<ResponsePlaceOpenApi.Document>(){{
                            add(ResponsePlaceOpenApi.Document.builder().title("A은행").address("2-1").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("B은행").address("2-2").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("C은행").address("2-3").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("D은행").address("2-4").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("E은행").address("2-5").build());
                        }},
                        //expected
                        new ArrayList<ResponsePlace>(){{
                            add(ResponsePlace.builder().title("신한은행").address("1-1").build());
                            add(ResponsePlace.builder().title("국민은행").address("1-2").build());
                            add(ResponsePlace.builder().title("NH농협은행").address("1-3").build());
                            add(ResponsePlace.builder().title("우리은행").address("1-4").build());
                            add(ResponsePlace.builder().title("KB국민은행").address("1-5").build());
                            add(ResponsePlace.builder().title("A은행").address("2-1").build());
                            add(ResponsePlace.builder().title("B은행").address("2-2").build());
                            add(ResponsePlace.builder().title("C은행").address("2-3").build());
                            add(ResponsePlace.builder().title("D은행").address("2-4").build());
                            add(ResponsePlace.builder().title("E은행").address("2-5").build());
                        }}
                ),
                // 특정 api에서 호출된 결과가 적은 경우 다른 api에서 추가로 호출하는지 확인
                // 1번째 api에서 추가 호출을 하므로 1번재 api의 값들은 모두 중복값으로 된다.
                Arguments.of(
                        new ArrayList<ResponsePlaceOpenApi.Document>(){{
                            add(ResponsePlaceOpenApi.Document.builder().title("신한은행").address("1-1").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("국민은행").address("1-2").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("NH농협은행").address("1-3").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("우리은행").address("1-4").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("KB국민은행").address("1-5").build());
                        }},
                        new ArrayList<ResponsePlaceOpenApi.Document>(){{
                            add(ResponsePlaceOpenApi.Document.builder().title("A은행").address("2-1").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("B은행").address("2-2").build());
                            add(ResponsePlaceOpenApi.Document.builder().title("C은행").address("2-3").build());
                        }},
                        //expected
                        new ArrayList<ResponsePlace>(){{
                            add(ResponsePlace.builder().title("신한은행").address("1-1").build());
                            add(ResponsePlace.builder().title("국민은행").address("1-2").build());
                            add(ResponsePlace.builder().title("NH농협은행").address("1-3").build());
                            add(ResponsePlace.builder().title("우리은행").address("1-4").build());
                            add(ResponsePlace.builder().title("KB국민은행").address("1-5").build());
                            add(ResponsePlace.builder().title("A은행").address("2-1").build());
                            add(ResponsePlace.builder().title("B은행").address("2-2").build());
                            add(ResponsePlace.builder().title("C은행").address("2-3").build());
                        }}
                )
        );
    }
}