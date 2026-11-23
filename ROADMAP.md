# Event Booking — backend roadmap

A Spring Boot application built from scratch, one concept per session, to reach the depth expected of a
backend developer with **~2 years of experience**: build it, test it, deploy it, and defend the design in an
interview. The UI comes later (see [When the UI joins](#when-the-ui-joins)).

**How we work:** I explain the concept and set the task. **You write the code.** I review it line by line and
explain what to improve. Every module ends with an interview angle on the same topic.

**Session size:** 30–60 minutes. Each module is self-contained — you can stop after any one.

**Goal:** interview-ready depth. Topics are ordered so that the ones interviewers probe hardest (transactions,
locking, JPA internals, security, system design) get the most room.

**Git workflow:** one branch per module (`module-05-n-plus-one`), opened as a pull request on GitHub. The review
happens on the PR, CI must be green, then it is merged into `main`. This is how real teams work, and it is
practice you get for free on every module.

---

## What we're building

A ticketing system for events. Chosen because it forces the hard topics naturally rather than artificially:

- **Two people buy the last seat at the same time** → optimistic locking, transaction isolation
- **A held seat must be released after 10 minutes** → scheduling, state machines
- **A seat map is read constantly and changes rarely** → caching
- **Listing events with filters and paging** → Specifications, `Pageable`, the N+1 problem
- **Money is involved** → `BigDecimal`, idempotency, audit trail
- **A payment provider is slow or down** → timeouts, retries, circuit breakers
- **50,000 people want 5,000 seats at 10:00** → the classic system design interview question

Core domain: `Venue` → `Event` → `Seat` → `Booking` → `Payment`, plus `User`.

---

## Stack, and why

| Choice | Why this and not the alternative |
| --- | --- |
| Java 21 | Current LTS. Records, pattern matching, virtual threads are fair interview questions now. |
| Spring Boot 4.x | Current line (the project uses 4.2). Spring Framework 7, Spring Security 7, `ProblemDetail`, observability built in. Older tutorials target 3.x — most of it still applies, starter names differ. |
| **PostgreSQL** | Not MySQL. More common in modern backend roles, better transaction/locking semantics to learn on. Runs in Docker, nothing to install. |
| **Flyway** | Not `ddl-auto=update`. Versioned migrations are how real projects manage schema — and `ddl-auto` in production is a standard interview trap. |
| **Testcontainers** | Integration tests against real Postgres, not H2. H2 lies about SQL behaviour. |
| Docker Compose | Postgres + Redis + Kafka + the app, one command. Docker is in scope from day one, not bolted on at the end. |
| MapStruct | Generated mappers instead of hand-written ones. |
| Kafka | The most-asked message broker in backend interviews. Used for booking events and the outbox pattern. |
| GitHub Actions | CI on every pull request from Phase 0; builds and publishes the Docker image later. |

---

## Phase 0 — Foundation

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 0 | Project setup | `compose.yaml` with Postgres, datasource config, first run | Why Compose over installing Postgres |
| 1 | Flyway migrations | `V1__create_venue_event.sql`, first schema | Why never `ddl-auto=update` in production |
| 2 | CI from day one | GitHub Actions workflow: build + test on every PR | What CI should block, and how fast it must be |

## Phase 1 — Persistence

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 3 | Entities done right | `Venue`, `Event` with `LocalDateTime`, `BigDecimal`, enums | Why `String` dates and `double` money are bugs; `equals`/`hashCode` on entities |
| 4 | Repositories + `@DataJpaTest` | Derived queries, first integration test with Testcontainers | `@DataJpaTest` vs `@SpringBootTest` |
| 5 | Relationships | `Event` → `Seat` one-to-many, bidirectional, cascade | Owning side, `mappedBy`, orphan removal |
| 6 | **The N+1 problem** | Reproduce it, then fix with `JOIN FETCH` and `@EntityGraph` | *Asked in almost every JPA interview* |
| 7 | JPA lifecycle | Persistence context, dirty checking, `@Transactional(readOnly)` | Managed vs detached vs transient |
| 8 | **Indexes and query plans** | `EXPLAIN ANALYZE` a slow query, add the right index | B-tree vs composite indexes, why an index is ignored |

## Phase 2 — API layer

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 9 | REST + DTOs | Controllers, request/response records, MapStruct | Why never expose entities; HTTP methods and status codes done right |
| 10 | Validation | `@Valid`, custom constraint annotation | Where validation belongs across layers |
| 11 | Error handling | `@RestControllerAdvice` with RFC 9457 `ProblemDetail` | Designing error contracts |
| 12 | Paging & filtering | `Pageable`, `Specification`, sorting | Keyset vs offset pagination at scale |
| 13 | API docs | OpenAPI with springdoc — Swagger UI is your "frontend" until the real one | Contract-first vs code-first, API versioning |

## Phase 3 — Concurrency and transactions *(the heart of it)*

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 14 | Transactions | Propagation, rollback rules, **the self-invocation trap** | *Very commonly asked* |
| 15 | Isolation levels | Demonstrate dirty/non-repeatable/phantom reads | What each level actually prevents |
| 16 | **Optimistic locking** | `@Version`, make a real double-booking fail | *The flagship topic of this project* |
| 17 | Pessimistic locking | `SELECT FOR UPDATE`, deadlock, timeout | When to pick which, and the cost |
| 18 | Booking state machine | `HELD → CONFIRMED → CANCELLED`, illegal transitions rejected | Modelling state safely |

## Phase 4 — Cross-cutting

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 19 | Caching | Redis, `@Cacheable`, eviction on seat change | Cache invalidation, stampede, TTL choice |
| 20 | Scheduling | `@Scheduled` job releasing expired holds, ShedLock | Scheduling across multiple instances |
| 21 | Async & events | `@Async`, `@TransactionalEventListener`, virtual threads | Why `AFTER_COMMIT` matters; `CompletableFuture` |

## Phase 5 — Security

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 22 | Spring Security | Modern `SecurityFilterChain`, BCrypt, registration | Filter chain order |
| 23 | JWT, properly | Access + refresh tokens, correct 401 vs 403 | Stateless auth trade-offs, token revocation |
| 24 | Method security | `@PreAuthorize`, ownership checks, `@WithMockUser` tests | Roles vs authorities, testing security |
| 25 | API hardening | CORS, rate limiting (Bucket4j + Redis), OWASP top issues | What CORS does and does not protect |

## Phase 6 — Testing

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 26 | The testing pyramid | Unit, `@WebMvcTest`, `@DataJpaTest`, full `@SpringBootTest` | What each slice proves, and costs |
| 27 | Concurrency tests | Prove double-booking is impossible, with threads | How to test a race condition |

## Phase 7 — Integration and messaging

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 28 | Calling other services | Fake payment provider called with `RestClient`, tested with WireMock | Timeouts — why the default is dangerous |
| 29 | Resilience | Resilience4j retry, circuit breaker, bulkhead | Retry storms; when *not* to retry |
| 30 | **Idempotency** | `Idempotency-Key` header on payment, safe retries | *How to never charge twice* |
| 31 | Kafka basics | Publish `BookingConfirmed`, consumer sends a (fake) email | Partitions, consumer groups, ordering guarantees |
| 32 | **Transactional outbox** | DB write + event publish that can't get out of sync | Dual-write problem, at-least-once delivery |

## Phase 8 — Deploy and operate

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 33 | Docker | Multi-stage build, layered jars, full Compose stack | Image size, build caching |
| 34 | Config & secrets | `dev`/`prod` profiles, environment variables, no secrets in Git | 12-factor config |
| 35 | Observability | Actuator, Micrometer metrics, structured logs, tracing (OpenTelemetry) | What to monitor and why; logs vs metrics vs traces |
| 36 | CD pipeline | GitHub Actions builds the image and pushes it to a registry (GHCR) | Build once, deploy many; rollbacks |
| 37 | **Deploy to the cloud** | App on Azure Container Apps (or Render) + managed Postgres | Managed services vs self-hosting, zero-downtime deploys |
| 38 | Load testing | k6 script against the deployed app, find the bottleneck | Latency percentiles (p95/p99) vs averages |

## Phase 9 — System design

Partly code, partly design write-ups (`docs/design/*.md`) with diagrams — the way you'd answer on a whiteboard.

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 39 | Scaling the app | Run 3 instances behind a load balancer in Compose; find what breaks | Stateless services, sticky sessions, horizontal vs vertical |
| 40 | Scaling the database | Read replica, routing reads; connection pool sizing (HikariCP) | Replication lag, CAP in practice |
| 41 | Monolith vs microservices | Split `payment` out into its own service; saga for book → pay | When splitting is a mistake; choreography vs orchestration |
| 42 | Real-time updates | Live seat map with Server-Sent Events (WebSocket compared) | Polling vs SSE vs WebSocket |
| 43 | **Design: the flash sale** | Write-up + prototype: virtual queue, seat holds, rate limits | *"Design Ticketmaster" — a top system design question* |

---

## When the UI joins

**You don't need a UI for Phases 0–4.** Swagger UI (Module 13) and IntelliJ `.http` files test every endpoint
faster than any frontend would.

**The UI becomes worth it after Phase 5 (Security).** Some backend topics only make sense with a real browser
calling the API:

- **CORS** (Module 25) only exists because browsers enforce it — Postman and Swagger skip it entirely.
- **Where to store JWTs** (memory vs `localStorage` vs `HttpOnly` cookie) and refresh-token flows are
  browser problems.
- **Live seat map** (Module 42) needs something on screen to update.

**Plan:** finish Phase 5, then build a small Angular app (event list → seat map → book → my bookings) in a
separate repo while continuing with Phase 6 onwards. Since you already know Angular, the UI will be fast;
the backend stays the focus.

---

## Rules we follow

1. **You write the production code.** I review it. If I write something, it's an example you then adapt.
2. **Every module ends with a working, merged state.** No half-finished branches.
3. **Tests come with the feature**, not in a cleanup phase at the end.
4. **I will point out mistakes directly.** That is the entire value of the review.
5. **Ask "why" freely.** If you can't explain a line you wrote, we stop and fix that before moving on.

## Prerequisites (one-time)

- **JDK 21 on `JAVA_HOME`** — the machine currently defaults to JDK 17, so `./mvnw` fails.
- **Docker Desktop running** — needed from Module 0 for Postgres and Testcontainers.
