# LoL-Pedia

> 본 저장소는 기존 Private 레포지토리의 보안 정보(API Key 등) 보호를 위해 소스코드를 재구성하여 공개한 버전입니다.

> LCK 경기 데이터 조회 플랫폼

**기간:** 2026.01 ~ 2026.03 | **1인 개발** | Java 21, Spring Boot 3.5.7

---

## 핵심 성과

| 지표 | Before | After |
|------|--------|-------|
| 동시 처리 VU | 200 (CPU 한계) | 2,000 (10배 확장) |
| 평균 응답시간 | 153ms | 9ms (94% 감소) |
| L1/L2 throughput | 2,125/s | 3,211/s (+51%) |
| 쿼리 스캔 rows | 88,074 | 1,481 (98.3% 감소) |
| 캐시 스탬피드 에러율 | - | 0% (응답시간 변화 없음) |

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Backend | Java 21, Spring Boot 3.5.7, Spring Data JPA |
| Database | MySQL (RDS), Redis (ElastiCache) |
| Cache | Caffeine (L1) + Redis (L2), TwoLevelCache 직접 구현 |
| Infra | AWS EC2 (t3.small x2), NAT Instance, Bastion Host |
| CI/CD | GitHub Actions, AWS CodeDeploy, ECR |
| 모니터링 | CloudWatch |
| 부하테스트 | k6 |

---

## 아키텍처

![Architecture](![img.png](img.png))

---

## 주요 구현

### 1. Redis + L1/L2 계층 캐시로 CPU 병목 해소

- **문제**: 캐시 없이 200 VU에서 CPU 한계 → avg 153ms, RPS 122/s
- **해결**: Redis 캐싱 적용 → 2,000 VU 수용, avg 9ms (94% 감소)
- **심화**: Redis 네트워크 왕복 비용 해소를 위해 `TwoLevelCache` 직접 구현
  - L1(Caffeine, 인메모리 ~0.01ms) + L2(Redis ~2ms) 계층 구조
  - key 공간 분석 → 좁은 캐시(top8)만 L1 적용, 넓은 캐시(match)는 L2 전용
  - throughput 2,125/s → 3,211/s (+51%)

```java
public class TwoLevelCache extends AbstractValueAdaptingCache {
    private final Cache l1; // Caffeine (in-memory, ~0.01ms)
    private final Cache l2; // Redis (~2ms)

    @Override
    protected Object lookup(Object key) {
        ValueWrapper l1Hit = l1.get(key);
        if (l1Hit != null) return toStoreValue(l1Hit.get());

        ValueWrapper l2Hit = l2.get(key);
        if (l2Hit != null) {
            l1.put(key, l2Hit.get()); // L1 warm-up
            return toStoreValue(l2Hit.get());
        }
        return null;
    }
}
```

### 2. 캐시 스탬피드 방어

- **문제**: Redis 만료 시 다수 요청이 동시에 cache miss → DB 직격
- **해결**: L1(Caffeine TTL 3분)이 Redis 삭제 구간을 흡수
- **검증**: k6로 Redis 키 50ms마다 강제 삭제하며 3단계 시나리오 실행
  - 에러율 0%, 스탬피드 구간 응답시간 유의미한 변화 없음

### 3. Virtual Thread 도입 실패 → 원인 분석 후 롤백

- **시도**: p95 352ms 개선을 위해 Java 21 Virtual Thread 도입
- **결과**: avg 158ms → 201ms, throughput 2,516/s → 2,199/s (악화)
- **원인 분석**:
  - t3.small 2 vCPU = Carrier Thread 2개로 500 VU 스케줄링 오버헤드 폭증
  - Redis 응답 1~2ms로 I/O 블로킹 시간이 너무 짧아 mount/unmount 비용이 이득보다 큼
  - Lettuce 핀닝 이슈(synchronized → ReentrantLock) 확인 → 6.6.0에서 이미 수정됨
- **결론**: I/O가 빠른 환경에서 VT는 고사양 서버에서 유효한 기술

### 4. 쿼리 튜닝 (EXPLAIN 기반)

- **YEAR() 함수**: 컬럼에 함수 적용으로 인덱스 range scan 불가
  - `YEAR(match_date) = :year` → `match_date BETWEEN :start AND :end`
  - rows 88,074 → 1,481 (98.3% 감소)
- **OR 조건**: index_merge + filesort 발생
  - 단일 쿼리 → `findByTeamAId` / `findByTeamBId` 분리 → FK 인덱스 직접 활용
  - rows 8,771 → 117, filesort 제거

### 5. 보안

- **Jackson 역직렬화**: `BasicPolymorphicTypeValidator` allowlist 적용으로 임의 클래스 실행 차단
- **민감정보 관리**: AWS SSM Parameter Store에 application.yml, .env 저장 → 배포 시 주입

