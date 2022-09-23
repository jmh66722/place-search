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
