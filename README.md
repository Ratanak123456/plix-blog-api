# Blog Application API 🚀

A robust and scalable RESTful API built with Spring Boot 4 for the PlixBlog platform. This backend handles user authentication, post management, engagement tracking, and more.

## 🛠 Tech Stack

- **Framework:** [Spring Boot 4.0.5](https://spring.io/projects/spring-boot)
- **Language:** [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- **Database:** [PostgreSQL](https://www.postgresql.org/)
- **Migration:** [Flyway](https://flywaydb.org/)
- **Security:** [Spring Security](https://spring.io/projects/spring-security) with JWT (JSON Web Tokens)
- **Data Access:** Spring Data JPA (Hibernate)
- **Documentation:** Postman Collection included
- **Monitoring:** [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html)
- **Mapping:** [ModelMapper](http://modelmapper.org/)
- **Utilities:** [Lombok](https://projectlombok.org/)

## 🏗 Features

- **JWT Authentication:** Secure login and registration with token-based access control.
- **Post Management:** Full CRUD operations for blog posts with slugs and categories.
- **User Profiles:** Manage user information and author-specific data.
- **Email Service:** Integrated mail support for notifications and account actions.
- **Database Migrations:** Versioned schema changes managed by Flyway.
- **Optimized Storage:** Enhanced image URL storage using `TEXT` columns to support long CDN and social media links.
- **Security:** CSRF protection, CORS configuration, and password hashing.
- **Health Checks:** Probes and health endpoints via Actuator.

## 🚀 Getting Started

### Prerequisites

- JDK 21
- PostgreSQL
- Gradle (provided via `./gradlew`)

### Configuration

The application uses profiles (`dev`, `prod`). Configuration is managed via `application.yaml` and environment variables.

#### Required Environment Variables

| Variable | Description | Default (Dev) |
| :--- | :--- | :--- |
| `DB_URL` | PostgreSQL connection URL | `jdbc:postgresql://...` |
| `DB_USERNAME` | Database username | `blog_alqc_user` |
| `DB_PASSWORD` | Database password | `********` |
| `JWT_SECRET` | Base64 encoded secret for JWT | Provided in `dev` |
| `FRONTEND_URL` | Allowed CORS origins | `http://localhost:3000` |

### Running Locally

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd blogs-api
   ```

2. **Configure the database:**
   Ensure PostgreSQL is running and update the `DB_URL` in your environment or `application-dev.yaml`.

3. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```
   The API will be available at `http://localhost:8080`.

### Building for Production

Build an executable JAR:
```bash
./gradlew build -x test
```
The JAR will be located in `build/libs/`.

## 🐳 Docker Support

Build and run using the provided `Dockerfile`:

```bash
docker build -t blogs-api .
docker run -p 8080:8080 --env-file .env blogs-api
```

## 🧪 Testing

Run the test suite:
```bash
./gradlew test
```

## 📮 API Documentation

A Postman collection is available in the root directory: `blog-application-api.postman_collection.json`. Import it into Postman to explore and test the endpoints.

---

Built for the **PlixBlog** ecosystem.
