# 🎬 Movie Management System

## 📌 Giới thiệu

**AaaMovies_BE Management System** là một ứng dụng web cho phép người dùng:
- Xem thông tin phim và tập phim.
- Đăng ký, đăng nhập ,quên mật khẩu và khôi phục mật khẩu.
- ADMIN có thể quản lý phim, thể loại, quốc gia và người dùng.
- USER có thể xem phim, tập phim và đánh giá.

---

## 🎯 Mục tiêu dự án

- Cung cấp API quản lý phim, tập phim, người dùng.
- Đảm bảo bảo mật: xác thực và phân quyền.
- Hỗ trợ tìm kiếm phim theo nhiều tiêu chí: từ khóa, quốc gia, thể loại,...

---

## 🔧 Công nghệ sử dụng

| Thành phần | Công nghệ |
|------------|-----------|
| Backend | Spring Boot (Java) |
| Cơ sở dữ liệu | MySQL |
| ORM | Hibernate (Spring Data JPA) |
| Bảo mật | Spring Security (JWT, @PreAuthorize) |
| Logging | SLF4J + Logback |
| Build tool | Maven |
| Container hóa | Docker |
| Config | `application.properties` + biến môi trường |

---

## 📚 Danh sách API chính

### 🎥 MovieController (`/api/movies`)
#### Public API
- `GET /api/movies`: Lấy danh sách tất cả phim.

- `GET /api/movies/hot`: Lấy danh sách phim nổi bật (is_hot = true).

- `GET /api/movies/new`: Lấy danh sách phim mới (is_new = true).

- `GET /api/movies/series`: Lấy danh sách phim bộ (type = SERIES).

- `GET /api/movies/singles`: Lấy danh sách phim lẻ (type = SINGLE).

- `GET /api/movies/{movieId}/episodes`: Lấy danh sách tập phim của phim với movieId.

- `GET /api/movies/by-country/{countryId}`: Lấy danh sách phim theo quốc gia với countryId.

- `GET /api/movies/by-category/{categoryId}`: Lấy danh sách phim theo thể loại với categoryId.

- `GET /api/movies/search?value={keyword}`: Tìm kiếm phim theo từ khóa.
  
- `GET /movies/{movieId}/ratings`: Lấy những đánh giá theo phim

#### User API (Cần quyền `USER`)
- `POST /movies/{movieId}/ratings`: User đánh giá phim

#### Admin API (Cần quyền `ADMIN`) không tính API Cần quyền `USER`
- `POST`
- `PUT`
- `DELETE`
- `PUT`


### 🔐 AuthController (`/api/auth`)
- `POST /send-verification`: Gửi mã xác minh.
- `POST /verify-register`: Xác minh đăng ký.
- `POST /login`: Đăng nhập.
- `POST /forgot-password`: Quên mật khẩu.
- `POST /reset-password`: Đặt lại mật khẩu.

### 📂 Category, Country, User Controller
- CRUD + `toggle-active` cho thể loại, quốc gia, người dùng.

---

## 🚀 Tính năng tương lai

- Danh sách phim yêu thích.
- Quản lý diễn viên, đạo diễn.
- Ưu đãi cho người dùng mới.
- Tích hợp hoàn chỉnh Docker.

---

## 📦 Hướng dẫn khởi chạy

### 1. Cài đặt môi trường

- Java 17+
- MariaDB (tạo DB `movie_db`)
- Maven
- Docker (nếu dùng Docker)

### 2. Cấu hình `application.properties`

\`\`\`properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
\`\`\`

### 3. Thiết lập biến môi trường

\`\`\` IntelliJ IDEA: tạo file application-dev.properties
DB_URL=jdbc:mariadb://localhost:3306/movie_db?useUnicode=yes&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=your_password
\`\`\`

---

## 🐳 Triển khai với Docker

### Dockerfile

\`\`\`Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/movie-management-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
\`\`\`

### docker-compose.yml

\`\`\`yaml
version: '3.8'

services:
  app:
    build:
      context: .
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:mariadb://db:3306/movie_db?useUnicode=yes&characterEncoding=UTF-8
      - DB_USERNAME=root
      - DB_PASSWORD=your_password
    depends_on:
      - db
    networks:
      - movie-network

  db:
    image: mariadb:10.5
    environment:
      - MYSQL_ROOT_PASSWORD=your_password
      - MYSQL_DATABASE=movie_db
    ports:
      - "3306:3306"
    volumes:
      - db-data:/var/lib/mysql
    networks:
      - movie-network

networks:
  movie-network:
    driver: bridge

volumes:
  db-data:
\`\`\`

### Chạy ứng dụng

\`\`\`bash
mvn clean package
docker-compose up --build
\`\`\`

---

## 🧪 Hướng dẫn test API

Sử dụng Postman hoặc Swagger (`/swagger-ui.html` nếu có).

### Ví dụ đăng ký

\`\`\`http
POST /api/auth/send-verification
{
  "email": "newuser@example.com",
  "fullName": "Người Dùng Mới",
  "password": "password123"
}
\`\`\`

---

## 📁 Dữ liệu mẫu (`data.sql`)

- Đặt trong `src/main/resources`
- Run Script vào database
- Gồm: countries, categories, movies, episodes, roles, users...

---

## 👤 Tài khoản mẫu

| Email | Mật khẩu (mã hóa) | Vai trò |
|-------|-------------------|---------|
| `admin@gmail.com` | 'admin' | ADMIN |
| `user1@example.com` | '123456@An'| USER |

---
## Nguồn phim : ophim17.cc
---

## 📬 Liên hệ

> Dự án được phát triển cho mục đích học tập. 

---

**Chúc bạn sử dụng vui vẻ 🎉**
"# bv1a" 
