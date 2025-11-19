# godo-app

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