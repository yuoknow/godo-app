# godo-app

## Локальный запуск через Docker Compose

1. Убедитесь, что установлены `Docker` и `Docker Compose`.
2. Выполните сборку и запуск сервисов:
   ```bash
   docker compose up --build
   ```
3. Backend будет доступен по адресу `http://localhost:8080`, база данных PostgreSQL — на порту `5432`.

### Переменные окружения

Вы можете переопределить значения по умолчанию, экспортировав переменные перед запуском `docker compose`:

```bash
export DB_NAME=mydb
export DB_USER=myuser
export DB_PASSWORD=secret
docker compose up --build
```

Все переменные необязательные, значения по умолчанию: `godo / godo / godo`.