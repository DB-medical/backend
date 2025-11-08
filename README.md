# Medical System Backend

Spring Boot 기반 의료 정보 시스템의 백엔드 모듈로, 병원·약국·처방 관련 데이터를 JPA 엔티티로 관리하고 REST API 및 시큐리티 구성을 위한 토대를 제공합니다.

## 기술 스택
- Java 21, Spring Boot 3.5 (Web, Data JPA, Security)
- Gradle 8 래퍼
- MySQL 드라이버, Jakarta Persistence, Lombok
- Docker Compose(`compose.yaml`)를 통한 개발용 인프라 구성

## 프로젝트 구조
```
├── src
│   ├── main
│   │   ├── java/db/team12/medical
│   │   │   ├── domain        # JPA 엔티티
│   │   │   └── repository    # JpaRepository 인터페이스
│   │   └── resources         # application.yaml, static, templates
│   └── test/java/db/team12/medical
│       └── MedicalApplicationTests.java
├── docs                      # 추가 문서
├── AGENTS.md                 # 기여 및 커뮤니케이션 지침
└── compose.yaml              # 로컬 인프라 설정
```

## 시작하기
1. 필수 도구: JDK 21, Docker(선택), Gradle 래퍼 사용.
2. 의존성 설치 및 빌드
   ```
   ./gradlew clean build
   ```
3. 로컬 실행
   ```
   ./gradlew bootRun
   ```
   필요 시 `compose.yaml`을 참고해 데이터베이스 컨테이너를 띄웁니다.

## 테스트
단위/통합 테스트는 JUnit 5 + Spring Boot Test로 구성되어 있습니다.
```
./gradlew test
```

## 문서 및 기여 가이드
- 저장소 전반 규칙과 PR 작성 요령은 [AGENTS.md](AGENTS.md)를 참조하세요.
- 추가 기능 제안이나 버그 리포트는 이슈 템플릿(작성 시 제공 예정)을 따라 주세요.

## 라이선스
추후 명시 예정입니다. 외부 배포가 필요하면 담당자와 상의 후 라이선스를 지정해 주세요.
