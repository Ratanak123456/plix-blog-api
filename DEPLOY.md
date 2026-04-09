# Deployment Notes

## Required environment variables

- `SPRING_PROFILES_ACTIVE=prod`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `FRONTEND_URL`
- `MAIL_FROM`
- `MAIL_HOST`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

## Recommended production behavior

- Flyway manages schema creation and upgrades.
- Hibernate runs with `ddl-auto=validate`.
- SQL logging is disabled in `prod`.
- Health endpoint is available at `/actuator/health`.

## Container build

```bash
docker build -t blogs-api .
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host:5432/blog \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e JWT_SECRET=replace-with-a-long-base64-secret \
  -e FRONTEND_URL=https://your-frontend.example.com \
  -e MAIL_FROM=no-reply@example.com \
  -e MAIL_HOST=smtp.example.com \
  -e MAIL_USERNAME=user \
  -e MAIL_PASSWORD=pass \
  blogs-api
```
