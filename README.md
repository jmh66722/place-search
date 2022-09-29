# 장소 검색 서비스

- 기본 포트 : 8080
- API 문서화 경로 : /swagger-ui
- H2 Database Console : /h2-console
  - JDBC URL: jdbc:h2:./dbdata/place
  - User Name: place
  - Password: place


# Build And Run, Test

## build

- bootJar   
  ~~~
  ./gradlew bootJar
  ~~~
  빌드가 완료되면 `build/plcae.jar` 파일이 생성됩니다.

## Run   

- bootRun   
  ~~~
  ./gradlew bootRun
  ~~~
- jar run

  `bootJar` 로 빌드후에 `build/plcae.jar` 를 실행합니다.

  ~~~
  java -jar ./build/place-search.jar
  ~~~

## Test

- gradlew test
  ~~~
  ./gradlew test
  ~~~

- gURL test
  ~~~

  ~~~


## 프로젝트 구조
```
place-search
├── src         
│   ├── main
│   │   ├── aop     -> 관점지향 처리를 위한 패키지
│   │   ├── common  -> 공통 기능 패키지
│   │   │   ├── controller -> 공통, Error controller
│   │   │   ├── error -> 예외, 오류 처리
│   │   │   ├── swagger -> API 문서화 옵션
│   │   │   ├── validator -> 유효성 검증 관련
│   │   │   └── web -> web 옵션
│   │   └── {domain}-> 도메인 별로 패키지 분리
│   │       ├── controller -> API controller
│   │       ├── dto -> 데이터 입,출력을 위한 정의 class
│   │       ├── entity -> 도메인 entity
│   │       ├── repository -> DB 처리 관련 
│   │       └── service -> 비즈니스 로직 처리
│   └── test
├── settings.gradle
└── build.gradle
```

<br><br><br>

# 요구사항

## 서비스 요구사항

---


**1) 장소 검색**

`GET /v1/place?q={keyword}`

Response
```
200 Ok


[
  {
    "title": "카카오뱅크"
  }
  ...
]
```

<br><br>

**2) 검색 키워드 목록** 

`GET /v1/keyword/statistic`

Response
```
200 Ok

[
  {
    "keyword": "은행",
    "count": 10
  },
  {
    "keyword": "병원",
    "count": 5
  }
  ...
]
```

<br><br>


## 기술적 요구사항

### 동시성 이슈가 발생할 수 있는 부분을 염두에 둔 설계 및 구현

해당 프로젝트에서 구현된 API 중 **검색 키워드 목록 조회**는 "장소 검색 API" 요청시 사용된 데이터를 집계한 데이터를 보여줍니다. 
만약, 여러 사용자가 "장소 검색 API"를 동일한 키워드로 동시에 검색했다면 동일한 키워드에 대한 통계 데이터가 동시에 집계 요청 될 수 있습니다. 
이런 경우에 여러 트랜잭션에서 하나의 데이터에 접근하는 일이 발생하는데 최종 Commit된 데이터가 정확한 집계가 안 되어 있을 수 있습니다.

해결 방법을 처음엔 단순하게 2가지로 생각해 보았습니다.
1) Update 를 사용하지 않는다.   
- 테이블에 데이터를 Insert만 하고 통계 데이터를 조회시 집계함수를 사용하여 조회한다. Insert할 테이블의 PK는 **Auto_increment** 를 사용하여 트랜잭션의 동시성 이슈인 **Phantom read** 를 회피한다.

쉽게 말해서 모든 "장소 검색 API" 호출에 대한 로그를 저장하고 이 로그 데이터를 집계하여 보여줍니다. 하지만, 데이터가 많이 누적될 수록 집계함수를 사용한 조회 쿼리의 성능은 떨어질 것입니다. 비약적으로 많은 데이터가 누적될 경우에는 파티셔닝이나 샤딩과 같은 스케일링을 고려해야 할 것 입니다.

2) 격리수준 제어를 한다.   
- 통계용 테이블을 생성하고 "장소 검색 API" 호출시 통계 데이터를 수정하여 집계한다. 

누적되는 데이터의 총량은 적겠지만 "장소 검색 API"가 호출 될 때마다 Update를 수행해야 하므로 높은 수준의 격리수준을 설정해야만 하는데, "장소 검색 API"의 처리 로직 자체에 성능저하가 발생합니다. "장소 검색 API"가 주된 서비스라는 것을 감안하면 심각한 수준의 성능저하까지 예상해 볼 수 있습니다. 

<br>

조금 더 고민해보니, 위 2가지 방법을 병행하면 더 좋을 것 같다는 생각이 들었습니다.   
- **통계 테이블**과 **검색 이력 테이블**을 만들어서 "장소 검색 API" 호출 시에는 검색 이력 데이터를 insert 하고, 일정 주기마다 검색 이력 데이터를 집계하여 통계 테이블에 반영한다.

이렇게 한다면 "장소 검색 API"의 처리 로직에서 격리수준을 낮추어 성능 저하를 방지할 수 있고, 집계를 동시간대에 한 번의 요청으로만 처리하기 때문에 통계 데이터에 대한 원자성과 독립성도 지킬 수 있습니다.
단점이라면 데이터가 집계 되는 주기가 있기 때문에 완전히 실시간 데이터를 제공할 수는 없고, 이런 부분은 사용자에게 고지해야할 필요가 있습니다.

<br><br>

### 카카오, 네이버 등 검색 API 제공자의 “다양한” 장애 발생 상황에 대한 고려

외부 API 에서 발생하는 장애에 대해 사용자 측은 알 필요가 없습니다. 그러므로 비즈니스 로직에서 사용하는 외부 API 가 몇 개든 관계없이 사용자가 기대하는 결과는 하나 입니다.  비즈니스 로직에서는 대체가능한 로직으로 처리하거나 이마저도 실패하면 서비스 장애로 반환하도록 하겠습니다.

대체가능한 로직은 "캐싱"을 이용합니다. 외부 API 요청에 대한 결과값을 캐싱 해놓는다면 외부 API 호출에 문제가 생겼을 경우 캐싱된 결과를 반환해 줄 수 있습니다. 캐시 데이터는 일정 시간마다 삭제할 것인데, 캐시가 지워지기 전에 외부 API 장애가 복구되지 않는다면 반환해줄 캐시가 삭제된 후에는 캐시가 없어서 서비스 장애를 반환해 줄 것입니다.

<br><br>

### 구글 장소 검색 등 새로운 검색 API 제공자의 추가 시 변경 영역 최소화에 대한 고려

외부 API에서 호출할 기능을 interface로 정의하고, Component에서 외부 API 별로 구현하였습니다. 구현된 로직은 비즈니스 로직을 처리하는 Service 단에서 호출하기 원하는 외부 API interface를 Component로 가져와서 외부 API 호출을 하도록 하였습니다.

<br><br>

### 서비스 오류 및 장애 처리 방법에 대한 고려

Spring MVC 를 사용하기 때문에 장애 시점에 대해 먼저 생각해보았습니다. 크게 장애가 발생하는 지점이 두 가지로 나눌 수 있을것 같습니다.

1. Controller 로직 내에서 장애발생
   - Controller 로직 내에서의 오류 발생은 Spring에서 제공하는 @RestControllerAdvice 로 발생하는 exception 별로 적절한 response 를 반환하도록 하였습니다.
2. Controller 로직 이전의 Web context 부분
   - Web context는 Spring context 외부 영역으로, Spring과 별개로 핸들링 해야 합니다. 저는 @RestControllerAdvice 로 오류의 공통처리를 하고 싶어서 '/error' Controller를 구현하고 Web context 에서 오류 발생시 '/error' path 로 포워딩하여 Spring context로 들어올 수 있도록 했습니다. '/error' Controller 에서는 http 상태별로 exception을 발생시키도록 했습니다.

<br><br>

### 대용량 트래픽 처리를 위한 반응성(Low Latency), 확장성(Scalability), 가용성(Availability)을 높이기 위한 고려

<br>

### 지속적 유지 보수 및 확장에 용이한 아키텍처에 대한 설계

<br>