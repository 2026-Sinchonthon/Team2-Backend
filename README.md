# 촌놈들(Sinchonthon_Team2)

### 신촌을 더 맛있게 만드는 서비스
# 🍚신촌세끼

> **신촌을 더 맛있게, 친근하게, 신촌세끼!**

대학생들이 직접 신촌의 맛집을 발견하고 공유하는 **대학별 맛집 추천 서비스**입니다.

신촌세끼는 신촌의 맛집 정보를 지도에서 확인하고, 대학별 좋아요 데이터를 통해 **어느 대학 학생들이 해당 맛집을 추천하는지** 확인할 수 있도록 기획되었습니다.

---

## 프로젝트 소개

신촌에는 서강대학교, 연세대학교, 이화여자대학교, 홍익대학교 등 여러 대학이 밀집해 있어 대학생을 중심으로 다양한 음식점과 상권이 형성되어 있습니다.

하지만 신촌에는 많은 음식점과 복잡한 골목 및 상권이 존재하기 때문에, 신촌을 처음 방문하는 학생들이 **실제로 학생들이 많이 찾는 맛집을 찾기 어렵다는 문제**가 있습니다.

신촌세끼는 이러한 문제를 해결하기 위해 **대학생들의 직접적인 맛집 참여와 학교별 좋아요 데이터**를 활용합니다.

---

## 서비스 목표

기존 지도 서비스처럼 단순히 음식점의 위치를 제공하는 것을 넘어,

> **“우리 대학 학생들은 어디를 추천하는가?”**

를 확인할 수 있는 맛집 서비스를 만드는 것을 목표로 합니다.

---

## 주요 기능

### 1. 신촌 맛집 지도

신촌세끼에 등록된 맛집의 위치 정보를 기반으로 지도에 음식점을 표시할 수 있습니다.

맛집 데이터에는 다음 정보가 포함됩니다.

* 식당 이름
* 주소
* 위도
* 경도
* Kakao Place ID
* 태그
* 좋아요 수

전체 인기 맛집 또는 학교별 인기 맛집을 조회할 수 있습니다.

```http
GET /api/restaurants
GET /api/restaurants?university=SOGANG
```

> **프론트엔드:** Kakao 지도 Web API를 이용한 지도 UI 및 음식점 검색
> **백엔드:** 신촌세끼에서 관리하는 맛집 데이터 및 위치 정보 제공

---

### 2. Kakao 검색 결과 맛집 등록

프론트엔드에서 Kakao 지도 Web API로 검색한 음식점 중 신촌세끼에 등록되지 않은 식당을 신촌세끼 DB에 등록할 수 있습니다.

```http
POST /api/restaurants
```

요청 데이터에는 Kakao Place ID와 식당의 기본 정보 및 위치 정보가 포함됩니다.

```json
{
  "kakaoPlaceId": "123456789",
  "name": "예시 식당",
  "address": "서울특별시 서대문구 ...",
  "latitude": 37.559,
  "longitude": 126.936
}
```

Kakao Place ID를 기준으로 기존 식당을 확인하기 때문에 동일한 식당이 중복 등록되는 것을 방지합니다.

---

### 3. 대학별 좋아요

맛집에 대한 좋아요를 사용자와 식당의 관계로 저장합니다.

```text
User
  │
  │ 좋아요
  ↓
Like
  │
  ↓
Restaurant
```

`Like` 엔티티를 통해 어떤 사용자가 어떤 식당에 좋아요를 눌렀는지 관리합니다.

동일한 사용자가 동일한 식당에 중복으로 좋아요를 등록하지 못하도록 `user_id`와 `restaurant_id`에 Unique Constraint를 적용했습니다.

#### 좋아요 추가

```http
POST /api/restaurants/{restaurantId}/likes
```

#### 좋아요 취소

```http
DELETE /api/restaurants/{restaurantId}/likes
```

좋아요 추가와 취소는 JWT 인증이 필요합니다.

---

### 4. 학교별 좋아요 데이터

맛집 조회 결과에는 전체 좋아요 수와 학교별 좋아요 수가 함께 포함됩니다.

현재 집계 대상 학교:

* `SOGANG`
* `YONSEI`
* `EWHA`
* `HONGIK`
* `MYONGJI`

응답 예시는 다음과 같습니다.

```json
{
  "restaurantId": 1,
  "kakaoPlaceId": "123456789",
  "name": "예시 식당",
  "address": "서울특별시 서대문구 ...",
  "latitude": 37.559,
  "longitude": 126.936,
  "likeCount": 10,
  "likeCountByUniversity": {
    "SOGANG": 2,
    "YONSEI": 4,
    "EWHA": 1,
    "HONGIK": 2,
    "MYONGJI": 1
  }
}
```

프론트엔드에서는 이 데이터를 이용하여 대학별 색상과 참여 정보를 표현할 수 있습니다.

---

## API

| Method   | Endpoint                                                | 설명                |
| -------- | ------------------------------------------------------- | ----------------- |
| `GET`    | `/api/restaurants`                                      | 전체 맛집 조회          |
| `GET`    | `/api/restaurants?university={university}`              | 학교별 인기 맛집 조회      |
| `GET`    | `/api/restaurants/{restaurantId}`                       | 맛집 상세 조회          |
| `POST`   | `/api/restaurants`                                      | 맛집 등록 또는 기존 맛집 조회 |
| `PATCH`  | `/api/restaurants/{restaurantId}`                       | 맛집 태그 수정          |
| `POST`   | `/api/restaurants/{restaurantId}/likes`                 | 좋아요 추가            |
| `DELETE` | `/api/restaurants/{restaurantId}/likes`                 | 좋아요 취소            |

---

## 백엔드 구조

```text
src
└── main
    ├── java
    │   └── org.example.team2backend
    │       ├── config
    │       │   ├── CorsConfig.java
    │       │   └── SwaggerConfig.java
    │       │
    │       ├── controller
    │       │   └── RestaurantController.java
    │       │
    │       ├── service
    │       │   └── RestaurantService.java
    │       │
    │       ├── repository
    │       │   ├── UserRepository.java
    │       │   ├── RestaurantRepository.java
    │       │   └── LikeRepository.java
    │       │
    │       ├── entity
    │       │   ├── User.java
    │       │   ├── Restaurant.java
    │       │   └── Like.java
    │       │
    │       └── dto
    │           ├── RestaurantRequest.java
    │           └── RestaurantResponse.java
    │
    └── resources
```

---

## Entity 구조

### Restaurant

맛집의 기본 정보를 저장합니다.

```text
Restaurant
├── id
├── kakaoPlaceId
├── name
├── address
├── latitude
├── longitude
├── description
├── createdAt
└── updatedAt
```

`kakaoPlaceId`에는 Unique Constraint가 적용되어 있습니다.

### User

현재 사용자 식별 및 학교별 좋아요 집계를 위해 학교 정보를 저장합니다.

```text
User
├── id
└── school
```

### Like

사용자와 맛집의 좋아요 관계를 저장합니다.

```text
Like
├── id
├── user_id
└── restaurant_id
```

`user_id + restaurant_id` 조합에 Unique Constraint를 적용하여 중복 좋아요를 방지합니다.

---

## 기술 스택

### Backend

* Java 21
* Spring Boot 4.1.1
* Spring Web MVC
* Spring Data JPA
* Spring Boot Actuator
* Lombok
* Gradle

### API Documentation

* Springdoc OpenAPI
* Swagger UI

### Database

* MySQL 8.0
* Docker Compose

### External API

* Kakao 지도 Web API

> Kakao 지도 검색 및 지도 UI는 프론트엔드에서 담당하며, 백엔드는 맛집 정보와 위치 데이터를 관리합니다.

---

## Database

Docker Compose를 이용하여 MySQL 8.0을 실행합니다.

### 실행

```bash
docker compose up -d
```

### 중지

```bash
docker compose down
```

### 데이터까지 삭제

```bash
docker compose down -v
```

MySQL 데이터는 `mysql_data` Docker volume을 통해 유지됩니다.

DB 정보:

```text
Database : sinchonthon
User     : sinchonthon
MySQL    : 8.0
Port     : 127.0.0.1:3306
Timezone : Asia/Seoul
```

DB 포트는 `127.0.0.1`에만 바인딩되어 외부에서 직접 접근할 수 없도록 구성되어 있습니다.

---

## API 문서

Swagger UI를 통해 백엔드 API를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

Swagger 설정은 `SwaggerConfig.java`에서 관리합니다.

---

## Frontend / Backend 역할

```text
[Frontend]

Kakao 지도 Web API
       ↓
음식점 검색
       ↓
지도 UI
       ↓
Backend API 호출
       ↓
[Backend]

Restaurant
Like
User
       ↓
맛집 및 좋아요 데이터 저장/조회
```

### Frontend

* Kakao 지도 Web API 연동
* 지도 UI
* 음식점 검색
* 카테고리 UI
* 학교별 좋아요 데이터 시각화

### Backend

* 맛집 데이터 저장 및 조회
* 맛집 위치 데이터 제공
* 카테고리별 조회
* 좋아요 저장 및 취소
* 전체 좋아요 수 집계
* 학교별 좋아요 수 집계

---

## 향후 개발 예정

현재 백엔드에 구현된 맛집 및 좋아요 기능을 기반으로 다음 기능을 확장할 예정입니다.

* 로그인 및 인증
* 학교 인증
* 인증 사용자 기반 데이터 수정 권한
* 맛집 목록 관리
* 맛집 수정 및 삭제
* 맛집 상세 정보
* 대학별 맛집 랭킹
* 지도 내 대학별 참여 정보 시각화

---

## 수익 모델

### 지역 음식점 광고

신촌세끼의 주요 사용자인 대학생을 대상으로 신촌 지역 음식점의 타겟 광고를 제공합니다.

### 음식점 제휴 프로모션

신촌 지역 음식점과 제휴하여 학생들에게 할인 및 쿠폰 등의 혜택을 제공하고, 광고비 또는 제휴 수수료를 확보하는 것을 목표로 합니다.

---

## 👥 Team 2

**신촌톤 Team 2**

> **신촌을 더 맛있게, 친근하게, 신촌세끼!**
