# 스포츠 예약 시스템 설계 문서

## 1. 요구사항 분석

### 1.1 기능 요구사항

#### 1.1.1 사용자 관리
- 회원가입, 로그인, 프로필 관리
- 사용자 역할 구분(일반 사용자, 시설 관리자, 시스템 관리자)
- 사용자 인증 및 권한 관리
- 회원 가입 시 이메일 인증
- 비밀번호 재설정 기능

#### 1.1.2 시설/코트 관리
- 시설 정보, 사진, 위치, 가격 등록 및 관리
- 스포츠 종류별 시설 등록 및 관리
- 시설 내 여러 코트(장소) 관리
- 운영 시간, 휴무일 설정
- 가격 정책 설정 (평일/주말, 시간대별 가격 차등)

#### 1.1.3 예약 시스템
- 날짜/시간 선택, 예약 생성 및 취소
- 중복 예약 방지
- 예약 상태 관리 (대기, 확정, 취소, 완료 등)
- 반복 예약 기능

#### 1.1.4 결제 시스템
- 온라인 결제, 환불 처리
- 결제 상태 관리
- 결제 내역 조회

#### 1.1.5 알림 기능
- 예약 확인, 변경 알림(이메일, SMS, 푸시 알림)
- 결제 관련 알림

#### 1.1.6 관리자 대시보드
- 예약 현황, 통계, 시설 관리
- 매출 통계
- 회원 관리

#### 1.1.7 추가 기능
- 소셜 기능(팀 구성, 매칭)
- 리뷰/평점 시스템
- 대기 예약 시스템

## 2. 데이터베이스 설계

### 2.1 엔티티 관계도 (ERD)

```
[USERS] --< [RESERVATIONS] >-- [COURTS] >-- [FACILITIES]
  ^            ^                  ^             ^
  |            |                  |             |
  |            v                  |             |
  |---< [REVIEWS] ----------------             |
  |                                            |
  |---< [TEAMS] >-< [TEAM_MEMBERS] >------    |
  |       ^                                |   |
  |       |                                |   |
  |       v                                |   |
  |    [MATCHING_REQUESTS] ----------------    |
  |                                            |
  |---- [FACILITY_MANAGERS] >-----------------
```

### 2.2 주요 엔티티 및 속성

#### 2.2.1 사용자(USERS)
- id (PK)
- email (Unique)
- password (암호화)
- name
- phone
- address
- profile_image
- role (ROLE_USER, ROLE_FACILITY_MANAGER, ROLE_ADMIN)
- enabled
- created_at
- updated_at

#### 2.2.2 시설(FACILITIES)
- id (PK)
- name
- description
- address
- latitude
- longitude
- contact_phone
- created_at
- updated_at

#### 2.2.3 코트(COURTS)
- id (PK)
- facility_id (FK)
- name
- sport_type
- description
- capacity
- indoor
- active
- created_at
- updated_at

#### 2.2.4 운영시간(OPERATING_HOURS)
- id (PK)
- facility_id (FK)
- day_of_week
- open_time
- close_time
- is_holiday

#### 2.2.5 가격정책(PRICE_POLICIES)
- id (PK)
- facility_id (FK)
- court_id (FK, nullable)
- name
- day_type (WEEKDAY, WEEKEND, HOLIDAY)
- start_time
- end_time
- price
- minimum_hours
- effective_from
- effective_to

#### 2.2.6 예약(RESERVATIONS)
- id (PK)
- user_id (FK)
- court_id (FK)
- start_time
- end_time
- status (PENDING, CONFIRMED, CANCELED, COMPLETED)
- cancel_reason
- canceled_at
- total_price
- created_at
- updated_at

#### 2.2.7 결제(PAYMENTS)
- id (PK)
- reservation_id (FK)
- imp_uid (결제 서비스 고유번호)
- merchant_uid (주문번호)
- amount
- status (READY, PAID, CANCELED, FAILED, REFUNDED)
- pay_method
- paid_at
- canceled_at
- cancel_reason
- created_at
- updated_at

#### 2.2.8 알림(NOTIFICATIONS)
- id (PK)
- user_id (FK)
- reservation_id (FK)
- type
- content
- method (EMAIL, SMS, PUSH)
- is_read
- sent_at
- read_at
- created_at

#### 2.2.9 리뷰(REVIEWS)
- id (PK)
- user_id (FK)
- facility_id (FK)
- rating
- content
- created_at
- updated_at

#### 2.2.10 시설 이미지(FACILITY_IMAGES)
- id (PK)
- facility_id (FK)
- image_url
- description
- is_main
- display_order
- uploaded_at

#### 2.2.11 대기 목록(WAITING_LIST)
- id (PK)
- user_id (FK)
- court_id (FK)
- date
- start_time
- end_time
- status (WAITING, NOTIFIED, RESERVED, CANCELED, EXPIRED)
- created_at
- updated_at

#### 2.2.12 팀(TEAMS)
- id (PK)
- name
- description
- created_at
- updated_at

#### 2.2.13 팀원(TEAM_MEMBERS)
- id (PK)
- team_id (FK)
- user_id (FK)
- role (OWNER, ADMIN, MEMBER)
- joined_at

#### 2.2.14 매칭 요청(MATCHING_REQUESTS)
- id (PK)
- team_id (FK)
- court_id (FK)
- preferred_time
- description
- status
- created_at
- updated_at

#### 2.2.15 시설 관리자(FACILITY_MANAGERS)
- id (PK)
- facility_id (FK)
- user_id (FK)
- role
- assigned_at

## 3. N:M 관계 매핑 테이블

### 3.1 User와 Facility 간의 관리자 관계 (N:M)
```
[USERS] --- [FACILITY_MANAGERS] --- [FACILITIES]
```

### 3.2 User와 Team 간의 관계 (N:M)
```
[USERS] --- [TEAM_MEMBERS] --- [TEAMS]
```

### 3.3 User와 Court 간의 대기 예약 관계 (N:M)
```
[USERS] --- [WAITING_LIST] --- [COURTS]
```

## 4. JPA 엔티티 설계 주요 고려사항

### 4.1 N:M 관계 처리
- 모든 N:M 관계는 중간 엔티티를 통해 두 개의 1:N, N:1 관계로 분리
- 예: User와 Team 간의 N:M 관계는 TeamMember 엔티티를 통해 관리

### 4.2 상속 관계 설계
- `@Inheritance` 어노테이션을 사용하여 상속 관계 설정 (필요한 경우)
- 예: 결제 방식이 다양한 경우 상속 구조 고려

### 4.3 Audit 정보
- Spring Data JPA의 `@CreatedDate`, `@LastModifiedDate` 사용
- `@EntityListeners(AuditingEntityListener.class)` 적용

### 4.4 Fetch 전략
- 기본적으로 @ManyToOne, @OneToOne은 LAZY 로딩 명시
- 성능 최적화를 위해 필요한 경우 명시적으로 FetchType 지정

## 5. RESTful API 설계 (기본 CRUD 엔드포인트)

### 5.1 사용자 관리 API
- POST /api/users - 회원가입
- POST /api/auth/login - 로그인
- GET /api/users/me - 내 정보 조회
- PUT /api/users/me - 내 정보 수정
- GET /api/users/{id} - 특정 사용자 정보 조회 (관리자)
- PUT /api/users/{id} - 특정 사용자 정보 수정 (관리자)

### 5.2 시설 관리 API
- POST /api/facilities - 시설 등록
- GET /api/facilities - 시설 목록 조회
- GET /api/facilities/{id} - 시설 상세 조회
- PUT /api/facilities/{id} - 시설 정보 수정
- DELETE /api/facilities/{id} - 시설 삭제
- POST /api/facilities/{id}/images - 시설 이미지 등록

### 5.3 코트 관리 API
- POST /api/facilities/{facilityId}/courts - 코트 등록
- GET /api/facilities/{facilityId}/courts - 코트 목록 조회
- GET /api/courts/{id} - 코트 상세 조회
- PUT /api/courts/{id} - 코트 정보 수정
- DELETE /api/courts/{id} - 코트 삭제

### 5.4 예약 관리 API
- POST /api/reservations - 예약 생성
- GET /api/reservations - 내 예약 목록 조회
- GET /api/reservations/{id} - 예약 상세 조회
- PUT /api/reservations/{id} - 예약 정보 수정
- DELETE /api/reservations/{id} - 예약 취소
- GET /api/courts/{courtId}/available-slots - 예약 가능 시간 조회

### 5.5 결제 API
- POST /api/payments/prepare - 결제 준비
- POST /api/payments/complete - 결제 완료
- GET /api/payments/{id} - 결제 정보 조회
- POST /api/payments/{id}/cancel - 결제 취소

### 5.6 리뷰 API
- POST /api/facilities/{facilityId}/reviews - 리뷰 작성
- GET /api/facilities/{facilityId}/reviews - 시설 리뷰 목록 조회
- PUT /api/reviews/{id} - 리뷰 수정
- DELETE /api/reviews/{id} - 리뷰 삭제

## 6. 보안 설계

### 6.1 인증 방식
- JWT(JSON Web Token) 기반 인증
- Access Token과 Refresh Token 사용

### 6.2 권한 관리
- Spring Security와 Custom UserDetailsService 구현

### 6.3 데이터 보안
- 비밀번호 해싱 (BCrypt)
- HTTPS 통신
- 입력 값 검증 및 방어적 코딩

## 7. 배포 및 인프라 구성

### 7.1 개발 환경
- Java 17
- Spring Boot 3.4.3
- MySQL
- Redis (캐싱, 세션 관리) 필요한 경우에만!

### 7.2 배포 환경
- Docker 컨테이너화
- CI/CD 파이프라인 구성 (GitHub Actions)
- AWS EC2, S3