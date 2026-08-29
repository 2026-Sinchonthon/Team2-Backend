# 🍚 신촌세끼 (Sinchon-Segi) · Backend

> **신촌을 더 맛있게, 친근하게, 신촌세끼!**

<p>
  <img alt="Java" src="https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white">
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white">
  <img alt="Gradle" src="https://img.shields.io/badge/Gradle-Wrapper-02303A?logo=gradle&logoColor=white">
  <a href="../../actions/workflows/ci-check.yml"><img alt="CI" src="https://github.com/2026-Sinchonthon/Team2-Backend/actions/workflows/ci-check.yml/badge.svg"></a>
  <a href="../../actions/workflows/deploy.yml"><img alt="Deploy" src="https://github.com/2026-Sinchonthon/Team2-Backend/actions/workflows/deploy.yml/badge.svg"></a>
</p>

신촌 5개 대학 학생들이 **직접 다녀온 맛집을 "완료" 체크**하고, 그 데이터를 지도 위에서
학교별로 확인하는 서비스의 **백엔드 API 서버**입니다.

---

## 무엇을 하는 프로젝트인가요

신촌에는 서강대·연세대·이화여대·홍익대·명지대가 밀집해 있지만, 정작 **"우리 학교 학생들이
진짜 가는 집"** 을 찾기는 어렵습니다. 일반 지도 앱의 별점은 학생 상권의 맥락을 담지 못하기 때문입니다.

신촌세끼는 이 질문에 답합니다.

> **"우리 대학 학생들은 어디를 추천하는가?"**

이 저장소는 그 판단의 근거가 되는 **맛집 데이터·완료 집계·학교별 랭킹**을 제공하는 API 서버입니다.
지도 UI와 카카오 장소 검색은 프론트엔드가 담당하고, 백엔드는 등록된 맛집과 집계 결과만 관리합니다.

---

## 왜 유용한가요

### 🏆 학교별 1위 랭킹 알고리즘

단순히 "우리 학교 학생이 많이 간 집"이 아니라, **다른 어느 학교보다 우리 학교가 더 많이 간 집**만
학교별 목록에 노출합니다. 덕분에 모든 학교에서 똑같은 유명 체인점만 반복되지 않고,
학교마다 실제로 다른 목록이 나옵니다. (동점이면 동점인 학교 모두에 표시)

### 📈 등록/삭제 API가 없는 설계

맛집의 "등록"과 "삭제"는 **상태를 바꾸는 행위가 아니라 조회 조건**입니다.
전체 완료 수가 **10개 이상**이 되면 목록에 자동으로 나타나고, 9개로 떨어지면 사라집니다.
운영자가 맛집을 큐레이션할 필요도, 별도의 승인 API도 없습니다.

### ⚡ N+1 없는 집계

맛집마다 카운트 쿼리를 날리는 대신, `GROUP BY` 한 번으로 전체·학교별 완료 수를 한꺼번에 집계합니다
([`CheckRepository`](src/main/java/org/example/team2backend/repository/CheckRepository.java)).
필터와 정렬도 전부 DB에서 처리합니다.

### 🔐 JWT 인증 + 일관된 응답 규격

모든 응답은 `{ success, data, error }` 형태로 통일되어 있어 프론트엔드의 분기 처리가 단순합니다.
조회(GET)는 비로그인도 열려 있고, 등록·완료·마이페이지만 토큰을 요구합니다.

---

## 시작하기

### 사전 요구사항

| 필요한 것 | 버전 | 비고 |
| --- | --- | --- |
| JDK | 21 | Gradle toolchain이 자동 인식 |
| Docker | 최신 | MySQL 컨테이너 실행용 |

Gradle은 별도 설치 없이 저장소에 포함된 Wrapper(`./gradlew`)를 사용합니다.

### 설치 및 실행

```bash
# 1. 저장소 클론
git clone https://github.com/2026-Sinchonthon/Team2-Backend.git
cd Team2-Backend

# 2. MySQL 컨테이너 기동 (127.0.0.1:3306)
docker compose up -d

# 3. 애플리케이션 실행 (기본 프로파일: local)
./gradlew bootRun
```

실행 후 아래 주소로 확인합니다.

| 항목 | 주소 |
| --- | --- |
| API 서버 | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| 헬스체크 | http://localhost:8080/actuator/health |

> **💡 스키마가 꼬였을 때**
> `ddl-auto: update`는 컬럼 삭제·이름 변경을 반영하지 않습니다.
> `docker compose down -v && docker compose up -d`로 볼륨째 초기화하세요.

### 테스트 & 빌드

```bash
./gradlew test          # 테스트 실행
./gradlew build         # 테스트 + 빌드
./gradlew bootJar       # 배포용 jar → build/libs/app.jar
```

### 환경 변수

`local` 프로파일은 [`application-local.yml`](src/main/resources/application-local.yml)의 기본값으로
바로 뜨지만, **운영 환경에서는 아래 값을 반드시 주입**해야 합니다.

| 변수 | 설명 | 기본값 |
| --- | --- | --- |
| `JWT_SECRET` | JWT 서명 키 | **없음 (미설정 시 부팅 실패)** |
| `DB_URL` | MySQL 접속 URL | `jdbc:mysql://127.0.0.1:3306/sinchonthon` |
| `DB_USERNAME` / `DB_PASSWORD` | DB 계정 | `sinchonthon` |
| `CORS_ALLOWED_ORIGINS` | 허용 오리진(쉼표 구분) | `http://localhost:3000,http://localhost:5173` |

`JWT_SECRET`은 의도적으로 기본값을 두지 않았습니다. 운영에서 빠뜨리면 조용히 취약해지는 대신
**부팅이 실패하도록** 설계했습니다.

---

## 사용 예시

### 1. 회원가입 → 토큰 발급

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "홍길동",
    "email": "test@example.com",
    "password": "Password1!",
    "passwordConfirm": "Password1!",
    "school": "SOGANG"
  }'
```

```json
{
  "success": true,
  "data": { "accessToken": "eyJhbGciOiJIUzI1NiJ9..." },
  "error": null
}
```

이후 인증이 필요한 요청에는 `Authorization: Bearer {accessToken}` 헤더를 붙입니다.

### 2. 맛집 등록 (= 완료 1개로 집계)

카카오 지도에서 검색한 장소를 신촌세끼에 등록합니다. **등록 신청 자체가 완료 1개**로 잡히고,
이미 등록된 장소면 `kakaoPlaceId` 기준으로 기존 맛집을 재사용합니다.

```bash
curl -X POST http://localhost:8080/api/restaurants \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "kakaoPlaceId": "123456789",
    "name": "예시 식당",
    "address": "서울특별시 서대문구 ...",
    "latitude": 37.559,
    "longitude": 126.936
  }'
```

### 3. 맛집 목록 조회

```bash
# 전체 보기 — 완료 10개 이상, 전체 인기순
curl http://localhost:8080/api/restaurants

# 학교별 보기 — 서강대가 학교별 완료 1위인 맛집만, 서강대 완료 순
curl "http://localhost:8080/api/restaurants?university=SOGANG"
```

```json
{
  "success": true,
  "data": {
    "university": "SOGANG",
    "restaurants": [
      {
        "restaurantId": 1,
        "name": "예시 식당",
        "latitude": 37.559,
        "longitude": 126.936,
        "checkCount": 14,
        "universityCheckCount": 8
      }
    ]
  },
  "error": null
}
```

> 전체 조회에서는 `university`와 `universityCheckCount` 필드가 응답에서 아예 빠집니다.

### 4. 상세 조회 & 완료 토글

```bash
# 상세 — 로그인 상태면 내 완료 여부(checked)까지 내려옵니다
curl http://localhost:8080/api/restaurants/1 -H "Authorization: Bearer $TOKEN"

# 완료 추가 / 취소
curl -X POST   http://localhost:8080/api/restaurants/1/checks -H "Authorization: Bearer $TOKEN"
curl -X DELETE http://localhost:8080/api/restaurants/1/checks -H "Authorization: Bearer $TOKEN"
```

완료 추가·취소 응답에는 **갱신된 집계가 함께** 내려오므로, 프론트는 목록을 다시 조회하지 않고
화면을 갱신할 수 있습니다.

```json
{
  "success": true,
  "data": {
    "restaurantId": 1,
    "checked": true,
    "checkCount": 15,
    "checkCountByUniversity": {
      "HONGIK": 1, "EWHA": 2, "YONSEI": 3, "SOGANG": 9, "MYONGJI": 0
    }
  },
  "error": null
}
```

### 5. 태그 수정

태그는 **전체 교체**입니다. 빈 배열을 보내면 모두 삭제됩니다.

```bash
curl -X PATCH http://localhost:8080/api/restaurants/1 \
  -H "Content-Type: application/json" \
  -d '{ "tags": ["혼밥", "해장"] }'
```

사용 가능한 태그는 다섯 가지로 고정되어 있으며, **한글 라벨 그대로** 주고받습니다.

`혼밥` · `공강` · `데이트` · `해장` · `밥약`

---

## API 한눈에 보기

전체 명세와 스키마는 실행 후 **[Swagger UI](http://localhost:8080/swagger-ui.html)** 에서 확인하세요.
상세 설계 배경은 [back.md](back.md), 구현 계획은 [plan.md](plan.md), 수동 검증 시나리오는
[postman-test.md](postman-test.md)에 정리되어 있습니다.

| Method | Endpoint | 설명 | 인증 |
| --- | --- | --- | :---: |
| `POST` | `/api/auth/signup` | 회원가입 (토큰 즉시 발급) | – |
| `POST` | `/api/auth/login` | 로그인 | – |
| `GET` | `/api/restaurants` | 전체 맛집 목록 (완료 10개 이상, 인기순) | – |
| `GET` | `/api/restaurants?university={school}` | 학교별 1위 맛집 목록 | – |
| `GET` | `/api/restaurants/{id}` | 맛집 상세 (로그인 시 `checked` 포함) | 선택 |
| `POST` | `/api/restaurants` | 맛집 등록 (= 완료 1개) | ✅ |
| `PATCH` | `/api/restaurants/{id}` | 태그 전체 교체 | – |
| `POST` | `/api/restaurants/{id}/checks` | 완료 추가 | ✅ |
| `DELETE` | `/api/restaurants/{id}/checks` | 완료 취소 | ✅ |
| `GET` | `/api/mypage/restaurants/checks` | 내가 완료한 맛집 목록 | ✅ |

`{school}`: `SOGANG` · `YONSEI` · `EWHA` · `HONGIK` · `MYONGJI` (대소문자 무관, 오타는 400)

### 응답 규격

성공·실패 모두 동일한 봉투(envelope)를 사용합니다.

```jsonc
// 성공
{ "success": true,  "data": { /* ... */ }, "error": null }

// 실패
{ "success": false, "data": null, "error": { "code": "NOT_FOUND", "message": "맛집을 찾을 수 없습니다." } }
```

| 코드 | HTTP | 상황 |
| --- | --- | --- |
| `VALIDATION_FAILED` | 400 | 요청 필드 검증 실패 (`data`에 필드별 사유) |
| `BAD_REQUEST` | 400 | 잘못된 학교명·태그, 중복 완료 등 |
| `PASSWORD_CONFIRM_MISMATCH` | 400 | 비밀번호 확인 불일치 |
| `UNAUTHORIZED` / `INVALID_CREDENTIALS` | 401 | 토큰 없음·만료, 로그인 실패 |
| `EMAIL_ALREADY_EXISTS` | 409 | 이미 가입된 이메일 |
| `NOT_FOUND` | 404 | 없는 맛집·사용자 |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 오류 |

> 로그인 실패는 이메일 없음과 비밀번호 오류를 **구분하지 않고** 동일하게 401을 반환합니다
> (가입 여부 노출 방지).

---

## 아키텍처

### 프로젝트 구조

```text
Team2-Backend/                       ← 저장소 루트 = Gradle 루트
├── .github/workflows/
│   ├── ci-check.yml                 ← PR 빌드·테스트 검증 (MySQL 서비스 컨테이너)
│   └── deploy.yml                   ← main push 시 EC2 배포 (빌드 → scp → 헬스체크 롤백)
├── deploy/                          ← systemd 유닛 · nginx 설정 · 서버 세팅 가이드
├── docker-compose.yml               ← MySQL 8.0 (127.0.0.1 바인딩)
└── src/main/java/org/example/team2backend/
    ├── auth/                        ← JWT 필터 · 토큰 발급 · 인증 principal
    ├── config/                      ← Security · CORS · Swagger · JPA Auditing
    ├── controller/                  ← Auth · Restaurant · MyPage
    ├── service/                     ← AuthService · RestaurantService (집계 로직)
    ├── repository/                  ← 랭킹·집계 JPQL
    ├── entity/                      ← User · Restaurant · Check · RestaurantTag · School
    ├── enums/                       ← RestaurantTagType (한글 라벨 직렬화)
    ├── exception/                   ← 에러 코드 · 전역 예외 핸들러
    └── response/                    ← ApiResponse 공통 봉투
```

### 도메인 모델

```text
   User ────< Check >──── Restaurant ────< RestaurantTag
    │                          │                 │
  school                 kakaoPlaceId          tagName
 (5개 대학)                 (unique)         (5종 고정 태그)
```

* **Check** — 사용자가 다녀온 맛집. `(user_id, restaurant_id)`에 UNIQUE를 걸어 중복을 막습니다.
  `User.school`을 타고 올라가 학교별 집계가 이루어집니다.
* **Restaurant** — 카카오 장소 기준 맛집. `kakaoPlaceId`가 UNIQUE라 중복 등록이 발생하지 않습니다.
* **RestaurantTag** — `(restaurant_id, tag_name)` UNIQUE. 수정은 전체 삭제 후 재삽입 방식입니다.

### 기술 스택

| 영역 | 사용 기술 |
| --- | --- |
| Language / Runtime | Java 21 |
| Framework | Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, Spring Security, Validation |
| Auth | JJWT 0.12.6, BCrypt |
| Database | MySQL 8.0 (Docker Compose) |
| Docs | springdoc-openapi 3.0.1 (Swagger UI) |
| Build / CI·CD | Gradle Wrapper, GitHub Actions, systemd + nginx (EC2) |
| Ops | Spring Boot Actuator |

### 배포

`main` 브랜치에 push하면 [`deploy.yml`](.github/workflows/deploy.yml)이 jar를 빌드해 EC2로 전송하고,
systemd 재시작 후 **헬스체크에 실패하면 이전 jar로 자동 롤백**합니다.
서버 최초 세팅(Docker·systemd·nginx·certbot) 절차는 [deploy/README.md](deploy/README.md)를 참고하세요.

---

## 도움이 필요하다면

| 무엇을 찾나요 | 어디로 |
| --- | --- |
| API 스펙·스키마 | [Swagger UI](http://localhost:8080/swagger-ui.html) (앱 실행 후) |
| 설계 배경과 정책 결정 | [back.md](back.md) |
| 구현 계획·작업 순서 | [plan.md](plan.md) |
| 요청/응답 예시 모음 | [postman-test.md](postman-test.md) |
| 서버 세팅·배포 트러블슈팅 | [deploy/README.md](deploy/README.md) |
| 버그 제보 | [Issues](../../issues/new?template=bug.md) |
| 기능 제안 | [Issues](../../issues/new?template=feature.md) |

---

## 기여하기

1. 이슈를 먼저 등록하고 (`bug` / `feature` 템플릿) 담당자를 지정합니다.
2. `main`에서 브랜치를 파고 (`feat/`, `fix/`, `refactor/` 접두사) 작업합니다.
3. PR을 열면 [`ci-check.yml`](.github/workflows/ci-check.yml)이 실제 MySQL 위에서 테스트와 빌드를 검증합니다.
4. [PR 템플릿](.github/pull_request_template.md)의 체크리스트를 채우고 리뷰를 요청합니다.

로컬에서 미리 확인하려면 push 전에 `./gradlew build`를 실행해 주세요.

---

## 팀

**신촌톤 2026 · Team 2 「촌놈들」**

| 파트 | 담당 |
| --- | --- |
| Backend | 맛집·완료 데이터 저장/조회, 학교별 집계·랭킹, 인증 |
| Frontend | 카카오 지도 Web API 연동, 지도 UI, 장소 검색, 학교별 데이터 시각화 |

> **신촌을 더 맛있게, 친근하게, 신촌세끼!** 🍚
