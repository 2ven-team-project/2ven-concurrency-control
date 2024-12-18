# 2ven-concurrency-control

# 동시성 제어
## 1. 팀원 소개

| 역할   | 이름   | 담당 업무                                                                                 |
|--------|--------|-------------------------------------------------------------------------------------------|
| 팀장   | 장민우 | 예매 시, 좌석클릭하면 Redis Lock                                                           |
| 팀원   | 심윤호 | 트래픽+도전과제동시성                                 |
| 팀원   | 서민기 | 공연 CRUD                     |
| 팀원   | 고의정 | 예매 CRUD+캐싱          |
| 팀원   | 박정완 | 인증인가/회원 CRUD , 인덱싱                                                                   |

---


## 2. 주요 백엔드 사용 기술

<div align="center">
    <img src="https://github.com/2ven-team-project/2ven-concurrency-control/blob/main/%EC%82%AC%EC%9A%A9%20%EA%B8%B0%EC%88%A0.png"/>
</div>

- **Spring Boot**: 애플리케이션의 주요 서버 사이드 프레임워크로 사용됩니다.

- **JPA (Java Persistence API)**: 데이터베이스와의 상호작용을 관리하는 데 사용됩니다.

- **MySQL**: 데이터 저장 및 관리에 사용되는 관계형 데이터베이스입니다.

- **JWT (JSON Web Token)**: 사용자 인증 및 보안 관리를 위해 사용됩니다.

- **Redisson**: Redis를 활용한 고급 기능을 제공하는 클라이언트 라이브러리입니다. 본 프로젝트에서는 **분산 락(distributed lock)**을 구현하여 높은 트래픽 환경에서 데이터 일관성을 유지하고, 순서를 보장하며 안정적으로 시스템이 동작하도록 지원합니다.

- **Spring Security**: 애플리케이션의 **인증(Authentication)** 및 **권한 부여(Authorization)**를 관리하는 강력한 보안 프레임워크입니다. JWT와 연계하여 사용자 인증을 처리하고, 권한 기반 접근 제어를 통해 애플리케이션의 보안을 강화합니다.


## 3. 기능 소개

### 회원 관리
- **회원가입**: 사용자가 이메일, 이름, 비밀번호를 입력하여 계정을 생성할 수 있습니다.
- **로그인**: 이메일과 비밀번호로 인증하여 시스템에 접근할 수 있습니다.
- **회원정보 수정**: 로그인한 사용자는 본인의 정보를 수정할 수 있습니다.
- **회원 탈퇴**: 사용자가 계정을 삭제하여 서비스를 더 이상 사용하지 않도록 할 수 있습니다.

---

### 공연 관리
- **공연 목록 조회**: 메인 페이지에서 등록된 모든 공연 정보를 확인할 수 있습니다.
- **공연 검색**: 특정 키워드를 사용하여 원하는 공연을 검색할 수 있습니다.
- **공연 상세 조회**: 선택한 공연의 자세한 정보를 확인할 수 있습니다.
- **공연 등록**: 관리자는 새로운 공연을 시스템에 등록할 수 있습니다.
- **공연 수정**: 이미 등록된 공연 정보를 수정할 수 있습니다.
- **공연 삭제**: 필요하지 않은 공연을 삭제할 수 있습니다.

---

### 예매 관리
- **공연 예매**: 사용자가 특정 공연의 좌석을 선택하고 예매할 수 있습니다.
- **예매 취소**: 이미 예매한 티켓을 취소할 수 있습니다.
- **예매 내역 조회**: 사용자가 본인의 예매 내역을 전체 또는 개별적으로 확인할 수 있습니다.
- **예매 좌석 관리**: 특정 공연의 좌석 현황을 확인하고, 원하는 좌석을 선택할 수 있습니다.

---

### 쿠폰 관리
- **쿠폰 조회**: 등록된 쿠폰 정보를 확인할 수 있습니다.
- **내 쿠폰 조회**: 사용자가 발급받은 쿠폰 내역을 확인할 수 있습니다.
- **쿠폰 등록**: 관리자가 새로운 쿠폰을 등록할 수 있습니다.
- **쿠폰 발급**: 사용자가 시스템에서 쿠폰을 발급받을 수 있습니다.

---

### 시스템 안정성
- **Redis Lock**: 높은 트래픽 환경에서도 데이터 충돌을 방지하고, 예매와 쿠폰 발급에서 순서를 보장하기 위한 안정적인 락 시스템을 제공합니다.




## 4. 트러블 슈팅

### 문제 해결: 쿠폰 발급 오류

1. **문제 확인**:
   - `Lock`을 적용했지만 쿠폰 발급이 정상적으로 이루어지지 않는 오류 발생.
   - 분석 결과, 짧은 시간 안에 요청이 몰리고 충돌이 빈번하게 발생하는 구조임을 확인.

2. **초기 구현 방식**:
   - 기존 `Lock` 로직은 DB를 조회하고 컬럼 값을 개별적으로 수정하는 방식으로 요청 처리.

3. **비관적 락(Pessimistic Lock) 적용**:
   - `LockModeType`을 `PESSIMISTIC_WRITE`로 변경하여 비관적 락을 설정.
   - DB 조회 시점에 락을 걸어 충돌 방지.
   - 결과: 쿠폰 발급이 정상적으로 처리되었으나 발급 순서가 보장되지 않는 문제가 발생(스핀 락 때문).

4. **Redisson 활용**:
   - Redisson의 `pub/sub` 구조를 사용하여 락 구현.
   - 요청 처리 순서를 보장하는 방식으로 개선.

5. **최종 결과**:
   - 쿠폰 발급 오류를 해결하고, 정상 처리와 발급 순서 보장을 모두 달성.

