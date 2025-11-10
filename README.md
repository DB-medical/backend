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
│   │   │   ├── repository    # JpaRepository 인터페이스
│   │   │   └── controller/service/security # REST API, 비즈니스 로직, JWT
│   │   └── resources         # application.yaml, static, templates
│   └── test/java/db/team12/medical
│       └── MedicalApplicationTests.java
├── docs                      # 추가 문서
├── AGENTS.md                 # 기여 및 커뮤니케이션 지침
├── compose.yaml              # 로컬 인프라 설정
└── README.md
```

### 계정 · 역할 모델

- `Member` 엔티티가 공통 로그인 정보(`username`, `password`, `name`, `role`)를 보유하고 `MemberRole` enum(`DOCTOR`, `PHARMACIST`)으로 권한을
  구분합니다.
- 의료진 상세 정보는 `Doctor`, `Pharmacist` 프로필 엔티티로 분리되어 각 도메인 연관(병원·진료과, 약국)을 유지합니다.
- 회원가입/로그인 요청은 `db.team12.medical.dto` 패키지의 `MemberSignupRequest`, `MemberLoginRequest` DTO를 통해 처리하며 역할별 필수 파라미터를 포함합니다.

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

## Docker / Compose 실행

1. `.env` 없이도 `compose.yaml`에 기본 환경 변수가 정의되어 있으므로 바로 실행 가능합니다.
   ```
   docker compose up --build
   ```
2. 컨테이너 구성
    - `mysql`: `medical` 데이터베이스를 생성하고 루트/일반 계정 비밀번호는 `compose.yaml`의 `MYSQL_*` 항목으로 정의되어 있습니다.
    - `backend`: 위 저장소의 Dockerfile로 빌드된 Spring Boot 애플리케이션. `SPRING_DATASOURCE_*`, `JWT_*` 환경 변수를 통해 DB 및 JWT 설정을 주입합니다.
3. 포트
    - MySQL: `3306` (호스트에 바인딩)
    - Backend: `8080` → `http://localhost:8080/swagger-ui/index.html`에서 Swagger UI 접근
4. 데이터 초기화
    - 애플리케이션 기동 시 `data.sql`이 실행되어 최소 10건 이상의 한글 더미 데이터가 로드됩니다.

## Swagger 기반 API 문서

- 애플리케이션 기동 후 `http://localhost:8080/swagger-ui/index.html`에 접속하면 OpenAPI 3.0 스펙과 인터랙티브한 요청 테스트 화면을 확인할 수 있습니다.
- JWT 인증이 필요한 API는 Swagger 우측 상단 Authorize 버튼을 눌러 `Bearer <token>` 값을 입력하면 됩니다.
- 예시 요청
  ```json
  // 회원 가입 (POST /signup)
  {
    "role": "DOCTOR",
    "email": "doctor.jsh@example.com",
    "name": "제승현",
    "password": "password",
    "passwordConfirm": "password",
    "doctorProfile": {
      "hospitalId": 1,
      "departmentId": 3
    }
  }
  ```
  ```json
  // 로그인 (POST /login)
  {
    "email": "doctor.jah@example.com",
    "password": "password",
    "role": "DOCTOR"
  }
  ```
- 모든 DTO에는 Swagger 주석이 포함되어 있어 필수 여부, 설명, 예시 값이 UI에 그대로 노출됩니다.

## 테스트

단위/통합 테스트는 JUnit 5 + Spring Boot Test로 구성되어 있습니다.

```
./gradlew test
```

## 문서 및 기여 가이드

- 저장소 전반 규칙과 PR 작성 요령은 [AGENTS.md](AGENTS.md)를 참조하세요.
- 도메인 및 요구사항 정리는 `docs/병원-약국 연동형 환자 진료, 처방 관리 시스템.md`에서 확인할 수 있습니다.
- 추가 기능 제안이나 버그 리포트는 이슈 템플릿(작성 시 제공 예정)을 따라 주세요.

## 인증 API

- `POST /signup`: `role`, `email`, `name`, `password`, `passwordConfirm`과 역할별 프로필(`hospitalId`/`departmentId` 또는
  `pharmacyId`)을 입력하면 `Member`와 대응 프로필을 생성합니다.
- `POST /login`: `email`, `password`, `role`을 받아 JWT 액세스 토큰을 발급합니다.
- `/login`, `/signup` 요청은 시큐리티 필터에서 제외되며, 그 외 요청은 `Authorization: Bearer <token>` 헤더를 통해 인증해야 합니다.
- JWT 관련 설정은 `application.yaml`의 `jwt.secret`, `jwt.expiration-millis`로 관리하며 환경 변수(`JWT_SECRET`, `JWT_EXPIRATION`)로
  오버라이드할 수 있습니다.

## 라이선스

추후 명시 예정입니다. 외부 배포가 필요하면 담당자와 상의 후 라이선스를 지정해 주세요.
