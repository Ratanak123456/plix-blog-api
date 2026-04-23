# PlixBlog API 🚀

A robust RESTful API built with Spring Boot for the PlixBlog platform.

## 🔗 Live Deployment & EDR

**Base URL:** https://plix-blog-api.onrender.com/api/v1
**ERD:** https://plix-blog-api.onrender.com/api/v1

## 🛠 Tech Stack

- **Framework:** Spring Boot 4.0.5
- **Language:** Java 21
- **Database:** PostgreSQL
- **Migration:** Flyway
- **Security:** Spring Security with JWT
- **ORM:** Spring Data JPA (Hibernate)

## 🏗 Features

- JWT Authentication (Login/Register)
- User Profile Management
- Post CRUD (Create, Read, Update, Delete)
- Categories & Tags Management
- Like, Bookmark, Comment System

## 📮 API Endpoints

### Auth
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token

### Profile
- `GET /api/v1/profile` - Get current user profile
- `PUT /api/v1/profile` - Update profile
- `PATCH /api/v1/profile/change-password` - Change password
- `GET /api/v1/profile/bookmarks` - Get user bookmarks

### Posts
- `GET /api/v1/posts` - List all posts
- `GET /api/v1/posts/{slug}` - Get post by slug
- `POST /api/v1/posts` - Create post
- `PUT /api/v1/posts/{id}` - Update post
- `DELETE /api/v1/posts/{id}` - Delete post
- `PATCH /api/v1/posts/{id}/like` - Toggle like
- `PATCH /api/v1/posts/{id}/bookmark` - Toggle bookmark

### Categories & Tags
- `GET /api/v1/categories` - List categories
- `GET /api/v1/tags` - List tags
- `POST /api/v1/tags` - Create tag

### Comments
- `GET /api/v1/comments/post/{postId}` - Get post comments
- `POST /api/v1/comments` - Create comment
- `DELETE /api/v1/comments/{id}` - Delete comment

## 🐳 Docker

```bash
docker build -t blogs-api .
docker run -p 8080:8080 blogs-api
```

## 🧪 Running Locally

```bash
cd blogs-api
./gradlew bootRun
```

API runs at `http://localhost:8080`

## 📮 Postman Collection

Import `blog-application-api.postman_collection.json` to test all endpoints.

---

Built with ❤️ for PlixBlog
