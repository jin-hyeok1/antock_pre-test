# 📦 PreTest Project

## 📁 패키지 구조

```
pretest/
├── business/
│   ├── api/                     # 외부 Open API 연동 (주소, 매출 상세)
│   ├── api/response/            # API 응답 DTO
│   ├── controller/              # REST 컨트롤러
│   ├── dto/                     # CSV 파싱용 DTO
│   ├── repository/              # JPA 레포지토리
│   ├── repository/entity/       # 엔티티 클래스
│   └── service/                 # 비즈니스 로직
│
├── common/                      # Custom WebClient 정의
├── config/                      # 공통 AOP 설정 등
├── property/                    # 외부 API 설정 값 (주소 API, 매출 CSV API 등)
└── PreTestApplication.java      # SpringBoot 메인 클래스
```

## 🔧 주요 기능
- ✅ 요청 파라미터: `city` (시/도), `district` (구/군)를 입력받아 통신판매 사업자 정보를 DB에 저장 
  - ✅ 통신판매 데이터(CSV)를 수신하여 법인 데이터 필터링
  - ✅ 공공 API를 통한 추가 정보 수집
  - ✅ 수집 완료 시 H2 DB로 데이터 저장

## 📌 고려 사항
- ✅ **법인등록번호 / 행정구역코드의 API의 변동 가능**
  - application.yml을 통해 값을 외부에서 주입할 수 있도록 구성하였으며, property 패키지를 통해 해당 값을 서비스 내부로 전달합니다.
  - 추후 API URL이나 인증 방식이 변경되더라도 설정 파일만 수정하면 쉽게 대응할 수 있도록 설계되었습니다.
- ✅ **현재는 DB에 저장하지만, 향후 저장소 변동 가능**
  - JPA 인터페이스 중심 구조로 설계되어 있어, 저장소 변경 시에도 비즈니스 로직의 수정 없이 application.yml 내 datasource 정보 및 의존성(dialect)만 변경하면 손쉽게 전환할 수 있습니다.
- ✅ **멀티쓰레드를 활용한 병렬 처리**
  - WebClient + Schedulers.boundedElastic()을 활용하여 I/O 병목 없이 데이터를 병렬로 처리하도록 구성했습니다.
  - 요청 단위의 비동기 작업은 Reactor 기반 Mono.zip으로 병렬 처리되며, CPU 연산과 격리된 쓰레드 풀에서 동작합니다.
- ✅ **멀티쓰레드 환경에서도 동시성 문제 없이 안전하게 작동해야 함**
  - 각 쓰레드에서 공유자원 없이 독립적으로 처리되도록 구성하여 Race Condition을 방지하였습니다.
- ✅ **유지보수가 용이한 로깅 처리**
  - AOP를 활용한 LogAspect 구성으로 모든 Controller 진입/예외 로그를 공통 처리하고 있습니다.
  - WebClient 요청 또한 커스텀 WebClient를 통해 HTTP Method, URI, 쓰레드 정보 등을 로그로 출력하도록 구성하여 디버깅 편의성을 높였습니다.
- ✅ **테스트코드**
  - 핵심 서비스 로직에 대해 @SpringBootTest 및 @DataJpaTest를 통해 통합 테스트를 작성하였으며, MockBean을 사용해 외부 API 호출은 테스트 환경에서 가짜 응답으로 대체됩니다.

## 🔗 API 명세
```
Method	URL                                                설명
POST	/mail-order-sales?city=(시/도)&district=(구/군)     시/군 단위로 OpenAPI → 필터링 → DB 저장 수행
```