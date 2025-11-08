# Repository Guidelines

## 프로젝트 구조 및 모듈 구성
백엔드 애플리케이션 코드는 `src/main/java/db/team12/medical` 아래에서 도메인 중심 패키지(`domain`, `repository`, 추후 `service`, `controller`)로 나뉩니다. 환경설정, 템플릿, 정적 리소스는 `src/main/resources`에, 테스트는 동일한 패키지 구성을 유지한 채 `src/test/java`에 위치합니다. 문서 자료는 `docs/`, 빌드 관련 스크립트는 루트(`build.gradle`, `gradlew*`)에 있습니다.

## 빌드 · 테스트 · 개발 명령
```
./gradlew clean build   # 전체 클린 빌드 및 테스트 실행
./gradlew test          # JUnit/Spring 기반 테스트만 실행
./gradlew bootRun       # 로컬 설정으로 Spring Boot 애플리케이션 기동
```
Gradle 래퍼(`./gradlew`)만 사용하여 버전 편차를 예방하고, 개발 중에는 `bootRun`과 DevTools를 활용해 빠르게 피드백을 얻습니다.

## 코딩 스타일 및 네이밍
기본 언어는 Java 21, 프레임워크는 Spring Boot 3.5입니다. 4칸 공백 들여쓰기, 120자 내외 줄 길이를 권장하고, 클래스는 PascalCase, 메서드·변수는 camelCase, 패키지는 소문자입니다. 엔티티는 `db.team12.medical.domain`, 리포지토리는 `...repository`, 설정은 `...config`에 배치합니다. Lombok이 이미 도입되어 있으므로 기존 패턴(`@Getter`, `@Builder`)과 일관된 사용만 허용합니다.

## 테스트 가이드라인
테스트 프레임워크는 JUnit 5이며 `@SpringBootTest` 기반의 통합 테스트가 기본입니다. 새 클래스는 `SomethingTests` 이름으로 `src/test/java`에 추가하고, 서비스·도메인 로직은 단위 테스트, JPA 매핑이나 시큐리티 흐름은 통합 테스트로 보강합니다. PR 전에는 반드시 `./gradlew test`를 실행해 CI 실패를 사전에 방지합니다.

## 커밋 · PR 작성 지침
커밋 메시지는 Conventional Commits 형식(`feat: …`, `fix: …`, `chore: …`)을 사용하며, 한 커밋에는 관련 코드·테스트·문서를 함께 포함합니다. PR 생성 시에는 ① 변경 요약(TL;DR), ② 주요 변경 목록, ③ 관련 이슈/티켓 번호, ④ API나 UI 변경 시 스크린샷 또는 `curl` 예시, ⑤ DB 스키마/마이그레이션 영향 여부를 명시합니다. 검토자가 재현할 수 있도록 환경 변수나 추가 스크립트가 있으면 함께 적습니다.

## 에이전트 커뮤니케이션 지침
이 저장소에 기여하는 에이전트는 모든 설명, 커밋 메시지, PR 코멘트를 한국어로 작성해야 합니다. 영어 인용이 필요한 경우에도 핵심 설명은 한국어로 먼저 제공하고, 제안 명령이나 로그 공유 시 `bash` 코드 블록 등을 활용해 재현 방법을 명확히 전달합니다.
