# Event Booking — backend learning roadmap

Learning backend development from the fundamentals to a production-ready application, through one project —
an event ticket-booking system — that grows topic by topic.

## Principles

1. **Bottom-up.** Each layer is learned before the tool built on top of it: HTTP and Java before Spring,
   SQL and JDBC before JPA.
2. **Feel the pain before the tool.** Every tool solves a problem. We hit the problem first (JDBC boilerplate,
   installing Postgres by hand, wiring objects manually), then learn the tool that removes it.
3. **One project that grows.** Plain Java → plain HTTP server → Spring with in-memory data → database →
   transactions → security → Docker → deployed → scaled.

## How every topic runs

Concept → Why we need it → Alternatives → Internal working → Implementation → Configuration →
Practical example → Exercise → Evaluation → next topic **only** when the current one is understood.

---

## Phase 1 — How backends work (no frameworks)

| # | Topic | Focus |
| --- | --- | --- |
| 1 | How the web works | Client/server, what happens during an API call, HTTP methods, status codes, headers, JSON |
| 2 | Java for backend work | Interfaces, annotations, exceptions, collections, records, generics |
| 3 | Maven | Why build tools exist, `pom.xml`, dependencies, lifecycle; Maven vs Gradle |
| 4 | A backend with no framework | Plain-Java HTTP server returning events as JSON; routing and JSON by hand |

## Phase 2 — Databases

| # | Topic | Focus |
| --- | --- | --- |
| 5 | Relational databases & SQL | Tables, keys, joins, constraints, normalisation; design the booking schema |
| 6 | PostgreSQL, the traditional way | Native install, SQL client (the pain Docker later removes) |
| 7 | JDBC, properly | Connections, `PreparedStatement`, `ResultSet`, SQL injection, manual transactions |
| 8 | Connection pooling | Why a connection per request fails under load; HikariCP |

## Phase 3 — Spring fundamentals

| # | Topic | Focus |
| --- | --- | --- |
| 9 | IoC & Dependency Injection | Manual wiring vs the Spring container, beans |
| 10 | Spring Boot internals | Auto-configuration, starters, embedded Tomcat, startup; properties vs YAML |
| 11 | Layered architecture | Controller / service / repository; package-by-layer vs by-feature |

## Phase 4 — REST APIs

| # | Topic | Focus |
| --- | --- | --- |
| 12 | Spring MVC internals | `DispatcherServlet`, handler mapping, Jackson; MVC vs WebFlux |
| 13 | REST API design | Resources, URLs, status codes, DTOs vs entities |
| 14 | Validation & error handling | `@Valid`, global exception handling, consistent error responses |
| 15 | Testing basics | JUnit 5, Mockito, controller tests |

## Phase 5 — Persistence with Spring

| # | Topic | Focus |
| --- | --- | --- |
| 16 | Spring JDBC | `JdbcTemplate` |
| 17 | ORM | JPA vs Hibernate vs Spring Data; trade-offs against JDBC |
| 18 | Entities & relationships | Venue → Event → Seat, change tracking |
| 19 | N+1 & performance | Fetching strategies, indexes, query plans |
| 20 | Schema migrations | Flyway; why not auto-generated schemas in production |

## Phase 6 — Transactions & concurrency

| # | Topic | Focus |
| --- | --- | --- |
| 21 | Transactions in Spring | `@Transactional` proxies, rollback rules, self-invocation |
| 22 | Race conditions | Reproduce two users booking the last seat |
| 23 | Isolation & locking | Isolation levels, optimistic vs pessimistic locking |
| 24 | Booking lifecycle | Held → confirmed → cancelled; scheduled hold expiry |

## Phase 7 — Security, configuration, Docker

| # | Topic | Focus |
| --- | --- | --- |
| 25 | Authentication & authorization | Sessions vs tokens, password hashing |
| 26 | Spring Security & JWT | Filter chain, JWT, roles, CORS |
| 27 | Configuration & logging | Profiles, secrets, logging |
| 28 | Docker | Images, containers, volumes, Compose; Docker vs traditional deployment |
| 29 | Integration testing | Testcontainers |

## Phase 8 — Production readiness

| # | Topic | Focus |
| --- | --- | --- |
| 30 | CI/CD | GitHub Actions |
| 31 | Observability | Health checks, metrics, logs, tracing |
| 32 | Caching | Redis, invalidation |
| 33 | Calling other services | Payment provider, timeouts, retries, idempotency |
| 34 | Messaging | Kafka, async processing |
| 35 | Cloud deployment | Live app with a managed database |

## Phase 9 — Architecture & system design

| # | Topic | Focus |
| --- | --- | --- |
| 36 | Monolith vs microservices | When splitting helps and when it hurts |
| 37 | Scaling | Multiple instances, load balancing, database replicas |
| 38 | Design exercise | Flash-sale ticketing system, interview style |

## Phase 10 — UI (Angular)

After Phase 7. CORS, token storage and live seat updates only make sense with a real browser client.

---

## Progress

| Topic | Status |
| --- | --- |
| 1 — How the web works | In progress |
