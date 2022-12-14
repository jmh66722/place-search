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
  빌드가 완료되면 `build/plcae-search.jar` 파일이 생성됩니다.

## Run   

- bootRun   
  ~~~
  ./gradlew bootRun
  ~~~
- jar run

  `bootJar` 로 빌드후에 `build/plcae-search.jar` 를 실행합니다.

  ~~~
  java -jar {프로젝트 루트 경로}/build/place-search.jar
  ~~~

## Test

- gradlew test
  ~~~
  ./gradlew test
  ~~~

## cURL Test
- Swagger
  API 문서화 경로 `/swagger-ui` 로 이동하면 Request 테스트를 해볼 수 있습니다.

- HttpRequest File Test
  `{프로젝트 루트 경로}/httpRequest` 에 `scratch.http` 파일이 있습니다.
  해당 파일의 cURL을 이용해 테스트 해 볼 수 있으며, Intellij IDE 를 사용한다면 IDE에서 파일을 열어 테스트 해볼 수 있습니다.


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

본래는 **이력 데이터**를 캐싱으로 이용하기 위해 `Redis no-SQL` 을 사용하려 했으나, M1 맥북 환경에서 Embedded Redis 실행시 문제가 있었습니다. 바이너리 파일로 실행시킬 수 있으나, 다른 OS 환경에서도 정상작동 하는지 확신이 서질않아서 Redis는 사용하지 않기로 했습니다. 덕분에 `H2 RDB`만을 데이터 저장 용도로 사용하였습니다.

현재 구현된 상태로는 **이력 데이터** 를 캐시 처럼 임시보관하여 일정시간 후에 지우지 않고 영구보관 하도록 되어있습니다. 그래서, **통계 데이터** 로 집계하여 저장하지 않아도 조회가 가능하지만 원래 계획대로 이력을 집계한 **통계 데이터**를 이용해 조회하도록 하였습니다.

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

- 반응성

  높은 반응성을 위해서 Webflux, RxJava 등의 비동기 방식을 고려했으나, 비즈니스 로직에서 외부 API 호출, 이력데이터 저장 등의 저장소 접근 등을 수행했을 때, Blocking 을 피해갈 수 있도록 하기에는 더 많은 고민이 필요했습니다. 동기식 프로세스에서도 높은 반응성을 위한 설계가 가능한지 저 스스로 더 고민해봐야할 문제인듯 합니다.

- 확장성

  프로젝트가 동시에 여러 장소에 분산되어 실행되고 있다면 **스케쥴러**가 동시에 실행될 가능성이 있습니다. ~~이를 방지하기 위해 `Shedlock` 이라는 라이브러리를 이용해 DB에 job을 저장하고 수행되고 있는 동일한 job은 일정시간 동안 실행되지 못하도록 합니다.~~ (M1 맥북 환경에서 해당 라이브러리 사용시 타임스탬프 오류가 있어서 제외했습니다.) 확장성을 위해 분산된 인스턴스에서 공통으로 수행되는 Batch, Scheduler 등의 Job이나, 캐싱, 데이터 등은 java 메모리에 저장하지 않고 외부 DB, 혹은 공유되는 스토리지에 저장하여야 할 것입니다.

- 가용성

  고가용성 서비스를 하기 위해선 다중 인스턴스 환경이 필요해 보입니다. 이는 확장성과도 연결이 되는데, 고가용성과 확장성을 위해  로드밸런싱과 컴포넌트 관리가 필수입니다. 이를 위해서 kubernetes, spring eureka 등의 클라우드 환경의 프로젝트 개발을 위한 솔루션들이 있습니다.

아쉽지만 이번 프로젝트에서 많은 부분을 실제 구현하지 못했습니다.

<br><br>

### 지속적 유지 보수 및 확장에 용이한 아키텍처에 대한 설계

객체 지향 프로그래밍은 지속적 유지보수와 확장에 용이한 설계를 위한 방법론 이라고 할 수 있습니다. 스스로도 모든 원칙을 지키진 못했지만 상기하고 발전을 고민하기 위해 원칙만 정리해 보겠습니다. 객체지향 설계 원칙은 5가지로 **단일 책임 원칙(SRP), 개발 폐쇄 원칙(OCP), 리스코프 치환 원칙(LSP), 인터페이스 분리 원칙(ISP), 의존성 역전 원칙(DIP) (SOLID)** 라고 많이 합니다.

- **단일 책임 원칙(SRP)**

  객체는 하나의 책임만을 맡아야 합니다. 하나의 클래스는 단일 역할만 수행하여 높은 응집도를 유지하고 결합도를 낮게 합니다.

  예를 들어, <u>장소 검색</u> 이라는 클래스가 있을 때, <u>장소 검색</u>이라는 역할을 여러 클래스에서 구현되지 않고, 마찬가지로 <u>장소 검색</u> 클래스에서 <u>통계 조회</u> 같은 다른 비즈니스 로직을 구현하지 않습니다.

- **개방 폐쇄 원칙(OCP)**

  객체의 확장은 개방적이게, 수정에는 폐쇄적이게 합니다.

  예를 들어, 외부에서 장소 정보를 받아 올 때, 장소 정보를 가져올 새로운 업체가 생긴다고 해서 장소 정보를 취합하는 로직이 변경될 필요는 없습니다. 그래서 <u>장소 정보 조회</u> 라는 인테페이스를 만들고, 업체별로 <u>장소 정보 조회</u> 기능을 구현합니다. 장소 정보를 취합하는 로직에서는 <u>장소 정보 조회</u> 인터페이스만 실행하여 취합하는 로직을 만들어 놓습니다. 

- **리스코프 치환 원칙(LSP)**

  하위 객체는 상위 객체들이 사용되는 곳으로 대체가 가능해야 합니다. 

  가장 간단하게 위배하지 않는 방법은 하위 클래스에서 상위 클래스의 메서드를 재정의 하지 않으면 됩니다.

- **인터페이스 분리 원칙(ISP)**

  객체는 자신이 호출하지 않는 메소드에 의존하면 안됩니다.

  예를 들어, <u>장소 검색</u> 인터페이스가 있고, 이를 구현한 구현체가 있을 때, 구현체는 외부API 에서 장소 검색하는 구현체가 있을 수 있고 로컬 스토리지의 정보를 제공해주는 구현체가 있을 수도 있습니다. 이는 각각 호출 방법이 다르고 데이터 취합 방법도 다르기 때문에 추가로 필요한 메서드는 각각 다를것입니다. 이렇게 구현체마다 추가로 작성한 메서드가 다른데 <u>장소 검색</u> 이라는 인터페이스에 모두 정의하면 구현체에서는 필요없는 기능까지 구현해야하는 상황이 발생할 수 있습니다.

- **의존성 역전 원칙(DIP)**

  추상화 된 것에 의존하게 하고, 구현체에는 의존하지 않도록 합니다. 구현하는 클래스는 인터페이스나 추상화 클래스만을 의존하여 확장성을 높이도록 합니다.

<br><br>