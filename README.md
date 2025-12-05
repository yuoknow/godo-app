# godo-app

Проект для управления мастер-классами с REST API на Spring Boot.

## Технологический стек

### Backend
- **Java 21** - современная версия Java
- **Spring Boot 3.5.7** - фреймворк для создания приложений
- **Spring Data JDBC** - работа с базой данных
- **PostgreSQL** - реляционная база данных
- **Lombok** - уменьшение boilerplate кода (геттеры, сеттеры, конструкторы)
- **JUnit 5** - тестирование с группировкой и современными практиками

### Инструменты разработки
- **Spotless** - автоматическое форматирование кода
- **Docker Compose** - контейнеризация базы данных

## Локальный запуск через Docker Compose

1. Убедитесь, что установлены `Docker` (включает `docker compose`) и Java 21.
2. Поднимите базу данных:
   ```bash
   docker compose up -d
   ```
3. Запустите backend:
   ```bash
   cd backend
   ./gradlew bootRun
   ```
4. Приложение доступно по адресу `http://localhost:8080`, база данных PostgreSQL — на порту `5432`.

5. Если нужно остановить приложение:
   lsof -ti :8080 | xargs kill

### Переменные окружения

Вы можете переопределить значения по умолчанию, экспортировав переменные перед запуском `docker compose`:

```bash
export DB_NAME=mydb
export DB_USER=myuser
export DB_PASSWORD=secret
docker compose up -d
```

Все переменные необязательные, значения по умолчанию: `godo / godo / godo`.

## Архитектура проекта

### Основные компоненты

#### 1. **MasterClass** (Сущность)
- Entity класс для работы с таблицей `master_classes`
- Использует Lombok для генерации геттеров/сеттеров
- Spring Data JDBC автоматически преобразует camelCase в snake_case (например, `durationMinutes` → `duration_minutes`)

#### 2. **MasterClassRepository**
- Интерфейс для работы с БД
- Наследуется от `CrudRepository`
- Автоматическая генерация SQL-запросов Spring Data

#### 3. **MasterClassController**
- REST контроллер с эндпоинтами:
  - `GET /api/master-classes` - получить все мастер-классы
  - `POST /api/master-classes` - создать новый мастер-класс
- Автоматически устанавливает `createdAt` при создании записи

## Тестирование

### Запуск тестов

```bash
cd backend
./gradlew test
```

### Структура тестов

Тесты организованы с использованием современных практик:

- **@Nested** - группировка тестов по функциональности
- **@DisplayName** - читаемые названия тестов
- Helper-методы - устранение дублирования кода

#### Группы тестов:

1. **GetAllMasterClassesTests** - тесты получения всех мастер-классов
2. **CreateMasterClassTests** - тесты создания мастер-классов

### Покрытие тестами

- ✅ Успешные сценарии (GET, POST)
- ✅ Получение пустого списка
- ✅ Получение всех записей
- ✅ Создание мастер-класса
- ✅ Автоматическая установка `createdAt`
- ✅ Создание и последующее получение записи

## Инструкция по тестированию API через Postman

## Базовый URL
```
http://localhost:8080
```

## 1. GET запрос - Получить все мастер-классы

**Метод:** `GET`  
**URL:** `http://localhost:8080/api/master-classes`


### Ожидаемый результат:
- **Статус:** `200 OK`
- **Тело ответа:** Массив мастер-классов (может быть пустым `[]`)

---

## 2. POST запрос - Создать новый мастер-класс

**Метод:** `POST`  
**URL:** `http://localhost:8080/api/master-classes`

### Примеры JSON для запроса:

#### Пример 1: Базовый мастер-класс
```json
{
  "title": "Java для начинающих",
  "description": "Изучите основы Java программирования",
  "instructor": "Иван Иванов",
  "durationMinutes": 120,
  "price": 5000.00
}
```

#### Пример 2: Продвинутый курс
```json
{
  "title": "Spring Boot продвинутый",
  "description": "Углубленное изучение Spring Boot фреймворка",
  "instructor": "Петр Петров",
  "durationMinutes": 180,
  "price": 8000.50
}
```

#### Пример 3: React курс
```json
{
  "title": "React с нуля",
  "description": "Изучите React с нуля до профи",
  "instructor": "Мария Сидорова",
  "durationMinutes": 240,
  "price": 10000.00
}
```

### Ожидаемый результат (успех):
- **Статус:** `201 Created`
- **Тело ответа:** Созданный объект с полями:
  ```json
  {
    "id": 1,
    "title": "Java для начинающих",
    "description": "Изучите основы Java программирования",
    "instructor": "Иван Иванов",
    "durationMinutes": 120,
    "price": 5000.00,
    "createdAt": "2024-01-15T10:30:00"
  }
  ```


## Работа с базой данных

### Подключение через DataGrip или другие инструменты

**Параметры подключения:**
- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `godo`
- **Username:** `godo`
- **Password:** `godo`

### Структура таблицы `master_classes`

```sql
CREATE TABLE master_classes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    instructor VARCHAR(255),
    duration_minutes INTEGER,
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

```

## Особенности реализации

### Lombok

Проект использует Lombok для уменьшения boilerplate кода:

- `@Getter` / `@Setter` - автоматическая генерация геттеров и сеттеров
- `@NoArgsConstructor` - конструктор без параметров
- `@RequiredArgsConstructor` - конструктор для final полей

### Маппинг полей на колонки БД

Spring Data JDBC автоматически преобразует имена полей из camelCase в snake_case:

- `durationMinutes` → `duration_minutes`
- `createdAt` → `created_at`
- `title` → `title` (без изменений)

Аннотация `@Column` не требуется, если имя колонки соответствует автоматическому преобразованию.

### Тестирование

Тесты написаны с использованием современных практик:

- Интеграционные тесты с реальной БД
- Группировка тестов с помощью `@Nested` для лучшей читаемости
- Helper-методы для устранения дублирования кода
- Использование `@DisplayName` для читаемых названий тестов