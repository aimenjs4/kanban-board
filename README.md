# Kanban Board API – Modul 295

REST-API für ein digitales Kanban Board mit Boards, Spalten und Aufgaben (Tasks).

## Autor
**Name:** Aimen Labidi  
**Kurs:** 21-295-E  
**Modul:** 295 – Backend für Applikationen realisieren

## Technologiestack
| Technologie | Version | Zweck |
|---|---|---|
| Java | 26 | Programmiersprache |
| Spring Boot | 4.0.6 | Application-Framework |
| Spring Data JPA / Hibernate | – | ORM / Datenbankzugriff |
| Spring Security + OAuth2 | – | Authentifizierung & Autorisierung |
| Keycloak | 25+ | OAuth2-Server (Realm: kanban-board) |
| PostgreSQL | 16 | Produktionsdatenbank |
| H2 | – | In-Memory-DB für Tests |
| springdoc-openapi | 3.0.1 | Swagger UI |
| Lombok | 1.18 | Boilerplate-Reduktion |
| JUnit 5 + MockMvc | – | Testing |
| Git / GitHub | – | Versionskontrolle |

## Projektstruktur
```
src/main/java/ch/aimen/labidi/kanban_board/
├── KanbanBoardApplication.java
├── board/          Board, BoardRepository, BoardService, BoardController
├── column/         BoardColumn, BoardColumnRepository, BoardColumnService, BoardColumnController
├── task/           Task, TaskRepository, TaskService, TaskController
├── user/           User, UserRepository, UserService, UserController
├── security/       SecurityConfig, OpenApiConfig
└── exception/      ResourceNotFoundException, GlobalExceptionHandler
```

## Rollen & Zugriffsrechte
| Rolle | Rechte |
|---|---|
| `ROLE_USER` | Boards, Columns, Tasks lesen; eigene Tasks erstellen und aktualisieren |
| `ROLE_ADMIN` | Vollzugriff auf alle Ressourcen (CRUD) |

## REST-Endpunkte (Übersicht)
| Methode | Pfad | Rolle | Beschreibung |
|---|---|---|---|
| GET | /api/boards | USER, ADMIN | Alle Boards |
| POST | /api/boards | ADMIN | Board erstellen |
| PUT | /api/boards/{id} | ADMIN | Board aktualisieren |
| DELETE | /api/boards/{id} | ADMIN | Board löschen |
| GET | /api/columns | USER, ADMIN | Alle Columns (optional ?boardId=) |
| POST | /api/columns | ADMIN | Column erstellen |
| PUT | /api/columns/{id} | ADMIN | Column aktualisieren |
| DELETE | /api/columns/{id} | ADMIN | Column löschen |
| GET | /api/tasks | USER, ADMIN | Alle Tasks (optional ?columnId=) |
| POST | /api/tasks | USER, ADMIN | Task erstellen |
| PUT | /api/tasks/{id} | USER, ADMIN | Task aktualisieren |
| DELETE | /api/tasks/{id} | ADMIN | Task löschen |
| GET | /api/users | ADMIN | Alle User |
| POST | /api/users | ADMIN | User erstellen |
| PUT | /api/users/{id} | ADMIN | User aktualisieren |
| DELETE | /api/users/{id} | ADMIN | User löschen |

## Lokaler Start

### Voraussetzungen
- JDK 26
- Docker (für PostgreSQL und Keycloak)

### 1. PostgreSQL starten
```bash
docker run -d --name kanban-postgres \
  -e POSTGRES_DB=kanban-board \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=Pas$1212 \
  -p 5432:5432 postgres:16
```

### 2. Keycloak starten
```bash
docker run -d --name keycloak -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:25.0.0 start-dev
```
Realm: `kanban-board`, Rollen: `USER`, `ADMIN`

### 3. Anwendung starten
```bash
./mvnw spring-boot:run
```

### 4. Swagger UI
```
http://localhost:9090/swagger-ui.html
```
Authorize-Button → Bearer Token aus Keycloak einfügen.

### 5. Token holen (curl)
```bash
curl -X POST http://localhost:8080/realms/kanban-board/protocol/openid-connect/token \
  -d "client_id=kanban-app" \
  -d "grant_type=password" \
  -d "username=<USER>" \
  -d "password=<PASSWORD>"
```

## Tests ausführen
```bash
./mvnw test
```
Tests laufen mit H2 in-memory (kein PostgreSQL, kein Keycloak nötig).

## Sicherheitsarchitektur
- Stateless REST API (kein Session-State)
- JWT-Validierung via Keycloak (`realm_access.roles` → Spring `ROLE_*`)
- Methoden-Sicherheit via `@PreAuthorize` auf jedem Controller-Endpunkt
- CSRF deaktiviert (stateless API)
- Swagger UI ohne Token erreichbar, alle API-Endpunkte erfordern Auth
