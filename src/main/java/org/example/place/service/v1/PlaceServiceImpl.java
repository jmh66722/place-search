package org.example.place.service.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.place.component.KakaoApi;
import org.example.place.component.LocalOpenApi;
import org.example.place.component.NaverApi;
import org.example.place.dto.response.ResponsePlace;
import org.example.place.dto.response.ResponsePlaceOpenApi;
import org.example.place.entity.SearchHistory;
import org.example.place.repository.SearchHistoryRepository;
import org.example.place.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {
    private final SearchHistoryRepository searchHistoryRepository;
    private final KakaoApi kakaoApi;
    private final NaverApi naverApi;

    @Autowired
    public PlaceServiceImpl(SearchHistoryRepository searchHistoryRepository, KakaoApi kakaoApi, NaverApi naverApi) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.kakaoApi = kakaoApi;
        this.naverApi = naverApi;
    }

    @Override
    @SneakyThrows
    @Transactional
    public List<ResponsePlace> getPlacesByKeyword(String keyword) {
        List<ResponsePlace> result = getPlacesByKeywordFromApis(keyword, kakaoApi, naverApi);

        searchHistoryRepository.save(SearchHistory
                .builder()
                .keyword(keyword)
                .result(new ObjectMapper().writeValueAsString(result))
                .build());

        return result;
    }


    private List<ResponsePlace> getPlacesByKeywordFromApis(String keyword, LocalOpenApi...apis) throws HttpClientErrorException, JsonProcessingException {
        int page = 1;
        int pageSize = 5;
        int targetSize = pageSize * apis.length;

        //merge할 documents 목록
        List<ResponsePlaceOpenApi.Document> mergeArguments[] = new ArrayList[apis.length];
        //api별로 불러온 documents 갯수
        int responseSizes[] = new int[apis.length];

        for(int i=0; i<apis.length; i++) {
            List<ResponsePlaceOpenApi.Document> documents = new ArrayList<>();
            try {
                documents.addAll(apis[i].getLocalByKeyword(keyword, PageRequest.of(page,pageSize), ResponsePlaceOpenApi.class).getDocuments());
            } catch (HttpClientErrorException e){
                //외부 api호출에 문제가 생겼을 경우, 이전 이력에서 조회, 이마저도 실패하면 에러 리턴
                String result = searchHistoryRepository.findTopByKeywordOrderByIdDesc(keyword).orElseThrow(() -> e).getResult();

                return new ObjectMapper().readValue(result, List.class);
            }
            mergeArguments[i] = documents;

            //호출된 documents 를 저장, 호출이 실패한 경우는 0
            responseSizes[i] = documents!=null ? documents.size() : 0;
        }
        
        //불러온 documents의 수량이 적은 api가 있을 경우 다른 api를 추가로 호출하기 위함
        Set<Integer> pageUpSet = new HashSet<>();
        for(int i=0; i<apis.length; i++) {
            //현재 호출한 api가 실패, 혹은 적게 호출 되었을 때
            if(responseSizes[i] < pageSize) {
                for(int index=0; index<apis.length; index++){
                    if(index == i || pageUpSet.contains(index)) continue;
                    //기대 수량(pageSize) 만큼 호출된 api 만이 다음 페이지도 호출될 가능성이 있으므로 다음 페이지 호출을 위해 추가
                    if(responseSizes[index] == pageSize) {
                        pageUpSet.add(index);
                        break;
                    }
                }
            }
        }

        for(int index:pageUpSet) {
            mergeArguments[index].addAll(apis[index].getLocalByKeyword(keyword, PageRequest.of(page+1,pageSize), ResponsePlaceOpenApi.class).getDocuments());
        }

        return mergeDocuments(targetSize, mergeArguments);
    }

    private List<ResponsePlace> mergeDocuments(int size, List<ResponsePlaceOpenApi.Document>...documents){
        //todo: 입력된 arguments 순서대로 우선순위가 결정된다. size보다 documents 가 많을 경우 우선순위가 높은 argument 부터 먼저 저장
        //메모리 낭비가 있을 것 같다.. 더 괜찮은 방법이 있는지 고민해봐야 할 듯

        final List<ResponsePlace> result = new ArrayList<>(size);
        final List<ResponsePlace> duplicated = new ArrayList<>(size);

        final List<ResponsePlace> merge = Arrays.stream(documents)
                .flatMap(Collection::stream)
                .map(doc -> ResponsePlace.builder()
                        .title(doc.getTitle().replace(" ",""))
                        .address(doc.getAddress())
                        .build())
                .collect(Collectors.toList());

        //중복체크를 위한 set
        final Set<String> set = ConcurrentHashMap.newKeySet();
        merge.forEach(place -> {
            if(!set.add(place.toString())) {
                duplicated.add(place);
                //todo: list를 다시 순회하기 때문에 성능저하가 있을 것이다. 순회하지 않는 다른 방법 생각해보기
                result.removeIf(p -> p.toString().equals(place.toString()));
            } else {
                result.add(place);
            }
        });

        result.addAll(0,duplicated);

        return result;

    }
}
