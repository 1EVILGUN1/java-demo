# Demo Spring Boot Application

Это демонстрационное приложение на Java с использованием Spring Boot, реализующее RESTful API для управления пользователями и аутентификации.

## Технологии

- **Java 17**
- **Spring Boot 3**
- **PostgreSQL** - база данных
- **Redis** - кэширование
- **Flyway** - миграции базы данных
- **MapStruct** - маппинг DTO
- **Testcontainers** - интеграционное тестирование
- **Swagger** - документация API
- **JWT** - аутентификация

## Требования

- JDK 17+
- Maven 3.8+
- Docker 20.10+ и Docker Compose

## Установка и запуск

1. Клонируйте репозиторий:
   ```bash
   git clone <URL_репозитория>
   cd java-demo
   ```

2. Запустите зависимости (PostgreSQL и Redis):
   ```bash
   docker-compose up -d
   ```

3. Соберите проект:
   ```bash
   mvn clean install
   ```

4. Запустите приложение:
   ```bash
   mvn spring-boot:run
   ```

Приложение будет доступно по адресу: http://localhost:8080

## API Документация

Документация API доступна через Swagger UI:
- http://localhost:8080/swagger-ui.html

## Конфигурация

Основные настройки находятся в `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/demo
    username: postgres
    password: password
  cache:
    type: redis
  data:
    redis:
      host: localhost
      port: 6379
```

## Основные эндпоинты API

### Аутентификация

**POST /api/auth/login** - аутентификация пользователя

Пример запроса:
```json
{
  "email": "john.doe@example.com",
  "phone": null,
  "password": "password123"
}
```

Пример ответа:
```json
{
  "token": "Bearer eyJhbGciOiJIUzI1NiJ9"
}
```

### Управление пользователями

**GET /api/users** - поиск пользователей с фильтрами

Параметры:
- `name` - фильтр по имени
- `dateOfBirth` - фильтр по дате рождения (формат dd.MM.yyyy)
- `email` - фильтр по email
- `phone` - фильтр по телефону
- `page` - номер страницы (по умолчанию 0)
- `size` - размер страницы (по умолчанию 10)

Пример запроса:
```
GET /api/users?name=John&dateOfBirth=15.05.1990&page=0&size=10
```

**GET /api/users/{id}** - получение пользователя по ID

**POST/PUT/DELETE /api/users/{id}/emails** - управление email пользователя

**POST/PUT/DELETE /api/users/{id}/phones** - управление телефонами пользователя

## Тестирование

Для запуска тестов:
```bash
mvn test
```

Тесты включают:
- Модульные тесты контроллеров (`@WebMvcTest`)
- Интеграционные тесты с Testcontainers (`@SpringBootTest`)

## Лицензия

Проект распространяется под лицензией [Apache License Version 2.0](LICENSE).