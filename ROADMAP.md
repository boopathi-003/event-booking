# Event Booking — learning roadmap

A Spring Boot application built from scratch, one concept per session, to reach solid mid-level depth.

**How we work:** I explain the concept and set the task. **You write the code.** I review it line by line and
explain what to improve. Every module ends with an interview angle on the same topic.

**Session size:** 30–60 minutes. Each module is self-contained — you can stop after any one.

**Goal:** interview-ready depth. Topics are ordered so that the ones interviewers probe hardest (transactions,
locking, JPA internals, security) get the most room.

---

## What we're building

A ticketing system for events. Chosen because it forces the hard topics naturally rather than artificially:

- **Two people buy the last seat at the same time** → optimistic locking, transaction isolation
- **A held seat must be released after 10 minutes** → scheduling, state machines
- **A seat map is read constantly and changes rarely** → caching
- **Listing events with filters and paging** → Specifications, `Pageable`, the N+1 problem
- **Money is involved** → `BigDecimal`, correctness, audit trail

Core domain: `Venue` → `Event` → `Seat` → `Booking` → `Payment`, plus `User`.

---

## Stack, and why

| Choice | Why this and not the alternative |
| --- | --- |
| Java 21 | Current LTS. Records, pattern matching, virtual threads are fair interview questions now. |
| Spring Boot 3.5.x | Current line. Spring Security 6 config style, `ProblemDetail`, observability built in. |
| **PostgreSQL** | Not MySQL. More common in modern backend roles, better transaction/locking semantics to learn on. Runs in Docker, nothing to install. |
| **Flyway** | Not `ddl-auto=update`. Versioned migrations are how real projects manage schema — and `ddl-auto` in production is a standard interview trap. |
| **Testcontainers** | Integration tests against real Postgres, not H2. H2 lies about SQL behaviour. |
| Docker Compose | Postgres + Redis + the app, one command. Docker is in scope from day one, not bolted on at the end. |
| MapStruct | Generated mappers instead of hand-written ones. |

---

## Phase 0 — Foundation

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 0 | Project setup | Initializr project, `docker-compose.yml` with Postgres, first run | Why Compose over installing Postgres |
| 1 | Flyway migrations | `V1__create_venue_event.sql`, first schema | Why never `ddl-auto=update` in production |

## Phase 1 — Persistence

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 2 | Entities done right | `Venue`, `Event` with `LocalDateTime`, `BigDecimal`, enums | Why `String` dates and `double` money are bugs |
| 3 | Repositories + `@DataJpaTest` | Derived queries, first integration test with Testcontainers | `@DataJpaTest` vs `@SpringBootTest` |
| 4 | Relationships | `Event` → `Seat` one-to-many, bidirectional, cascade | Owning side, `mappedBy`, orphan removal |
| 5 | **The N+1 problem** | Reproduce it, then fix with `JOIN FETCH` and `@EntityGraph` | *Asked in almost every JPA interview* |
| 6 | JPA lifecycle | Persistence context, dirty checking, `@Transactional(readOnly)` | Managed vs detached vs transient |

## Phase 2 — API layer

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 7 | REST + DTOs | Controllers, request/response records, MapStruct | Why never expose entities |
| 8 | Validation | `@Valid`, custom constraint annotation | Where validation belongs across layers |
| 9 | Error handling | `@RestControllerAdvice` with RFC 7807 `ProblemDetail` | Designing error contracts |
| 10 | Paging & filtering | `Pageable`, `Specification`, sorting | Keyset vs offset pagination at scale |

## Phase 3 — Concurrency and transactions *(the heart of it)*

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 11 | Transactions | Propagation, rollback rules, **the self-invocation trap** | *Very commonly asked* |
| 12 | Isolation levels | Demonstrate dirty/non-repeatable/phantom reads | What each level actually prevents |
| 13 | **Optimistic locking** | `@Version`, make a real double-booking fail | *The flagship topic of this project* |
| 14 | Pessimistic locking | `SELECT FOR UPDATE`, deadlock, timeout | When to pick which, and the cost |
| 15 | Booking state machine | `HELD → CONFIRMED → CANCELLED`, illegal transitions rejected | Modelling state safely |

## Phase 4 — Cross-cutting

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 16 | Caching | Redis, `@Cacheable`, eviction on seat change | Cache invalidation, stampede |
| 17 | Scheduling | `@Scheduled` job releasing expired holds | Scheduling across multiple instances |
| 18 | Async & events | `@Async`, `@TransactionalEventListener` | Why `AFTER_COMMIT` matters |

## Phase 5 — Security

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 19 | Spring Security 6 | Modern `SecurityFilterChain`, BCrypt, registration | Filter chain order |
| 20 | JWT, properly | Access + refresh tokens, correct 401 vs 403 | Stateless auth trade-offs, token revocation |
| 21 | Method security | `@PreAuthorize`, ownership checks, `@WithMockUser` tests | Roles vs authorities, testing security |

## Phase 6 — Testing

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 22 | The testing pyramid | Unit, `@WebMvcTest`, `@DataJpaTest`, full `@SpringBootTest` | What each slice proves, and costs |
| 23 | Concurrency tests | Prove double-booking is impossible, with threads | How to test a race condition |

## Phase 7 — Production

| # | Module | You build | Interview angle |
| --- | --- | --- | --- |
| 24 | Docker | Multi-stage build, layered jars, full Compose stack | Image size, build caching |
| 25 | Config & profiles | `dev`/`prod`, externalised secrets | 12-factor config |
| 26 | Observability | Actuator, custom metrics, structured logs, health checks | What to monitor and why |

---

## Rules we follow

1. **You write the production code.** I review it. If I write something, it's an example you then adapt.
2. **Every module ends with a working, committed state.** No half-finished branches.
3. **Tests come with the feature**, not in a cleanup phase at the end.
4. **I will point out mistakes directly.** That is the entire value of the review.
5. **Ask "why" freely.** If you can't explain a line you wrote, we stop and fix that before moving on.
