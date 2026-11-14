# Workout Tracker

Backend-приложение для ведения тренировок.

## Стек

- Java 17
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring Security
- JWT
- OpenAPI / Swagger
- Maven

## Возможности

- регистрация и авторизация пользователей;
- JWT-аутентификация;
- роли пользователей;
- каталог упражнений;
- создание, редактирование, просмотр и удаление тренировок;
- автоматическое применение Flyway-миграций.

## Требования

- Java 17+
- Docker и Docker Compose;
- Maven или Maven Wrapper.

## Настройка

Скопируйте пример переменных окружения:

```bash
copy .env.example .env
```

Для Linux/macOS:

```bash
cp .env.example .env
```

Измените значения в `.env`.

## Запуск PostgreSQL

```bash
docker compose up -d postgres
```

Проверить состояние контейнера:

```bash
docker compose ps
```

Остановить PostgreSQL:

```bash
docker compose down
```

## Запуск приложения

Через Maven Wrapper:

```bash
mvnw.cmd spring-boot:run
```

Для Linux/macOS:

```bash
./mvnw spring-boot:run
```

По умолчанию используется профиль `local`.

Запуск с конкретным профилем:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

## Сборка

```bash
mvnw.cmd clean package
```

## Тесты

```bash
mvnw.cmd test
```

Тестовый профиль использует настройки из:

```text
src/test/resources/application-test.properties
```

## API-документация

После запуска приложения:

- Swagger UI: http://localhost:8080/api/docs
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Основные endpoint’ы

### Авторизация

```text
POST /api/auth/register
POST /api/auth/login
```

### Пользователи
```text
GET /api/user/me
PATCH /api/user/me
```

### Упражнения

```text
GET  /api/exercises
GET  /api/exercises/{id}
GET  /api/exercises/name/{name}
POST /api/exercises
```

### Тренировки

```text
POST   /api/workouts
GET    /api/workouts/{id}
GET    /api/workouts
PATCH  /api/workouts/{id}
PUT    /api/workouts/{id}
DELETE /api/workouts/{id}
```

### Админ-панель

```text
GET /api/admin/users
PATCH /api/admin/users/{id}/role
```

Для защищённых endpoint’ов передавайте JWT:

```text
Authorization: Bearer <token>
```

## Миграции базы данных

Миграции находятся в:

```text
src/main/resources/db/migration
```

Используется Flyway. При запуске приложения миграции применяются автоматически.

Основные миграции:

- `V1__init.sql` — создание таблиц;
- `V1_1__seed_data.sql` — начальные упражнения и мышцы.
- `V1_2__add_sql_constraints.sql` - ограничения базы данных на пользователей и упражнения

## Структура проекта

```text
src/main/java/notroflguru/workouttracker
├── admin
├── config
├── bootstrap
├── exercise
├── user
├── workout
├── dto
├── GlobalExceptionHandler.java
└── WorkoutTrackerApplication.java
```

## Переменные окружения

| Переменная               | Назначение              |
|--------------------------|-------------------------|
| `POSTGRES_DB`            | имя базы данных         |
| `POSTGRES_USER`          | пользователь PostgreSQL |
| `POSTGRES_PASSWORD`      | пароль PostgreSQL       |
| `POSTGRES_PORT`          | внешний порт PostgreSQL |
| `JWT_SECRET`             | секрет для подписи JWT  |
| `INITIAL_ADMIN_LOGIN`    | логин для админа        |
| `INITIAL_ADMIN_PASSWORD` | пароль для админа       |
| `INITIAL_ADMIN_EMAIL`    | почта для админа        |

## Остановка

```bash
docker compose down
```

Для удаления базы вместе с volume:

```bash
docker compose down -v
```

Команда `down -v` удаляет данные PostgreSQL.