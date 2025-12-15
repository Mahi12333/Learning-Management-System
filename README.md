# 📚 Learning Management System (LMS)

A complete **Learning Management System** built with **Java 21 & Spring Boot**, designed to manage courses, users (students / teachers), groups, communities, assignments, submissions, and content. This project demonstrates clean architecture, modular design, role-based access control, JWT authentication, and production-ready backend practices.

---
## 🧩 Modules & Responsibilities

### 1️⃣ User Management Module

**Purpose:** Manage users and their roles.

**Responsibilities:**

* Register and authenticate users (Student, Teacher, Admin)
* Issue and validate JWT tokens
* Enforce role-based access control on APIs
* Manage user profiles and basic account data

---

### 2️⃣ Course Management Module

**Purpose:** Handle the lifecycle of courses.

**Responsibilities:**

* Create, update, and delete courses
* Assign instructors/teachers to courses
* Enroll students into courses
* Manage course categories, tags, and metadata
* Expose paginated and sortable course listings

---

### 3️⃣ Assignment & Submission Module

**Purpose:** Support teaching and evaluation workflow.

**Responsibilities:**

* Allow teachers to create assignments with deadlines and instructions
* Allow students to submit assignments (file or text)
* Track submission history and versions
* Record grades and feedback for each submission
* Provide assignment listings by course or student

---

### 4️⃣ Content Management Module

**Purpose:** Store and serve learning materials.

**Responsibilities:**

* Upload and manage course resources (videos, documents, notes, etc.)
* Store files locally or in cloud storage (Cloudinary / S3, if configured)
* Provide secure and controlled file download / access
* Optionally generate signed URLs for protected content

---

### 5️⃣ Notification Module

**Purpose:** Notify users about important events.

**Responsibilities:**

* Send email notifications for:
  * New course enrollment
  * Upcoming or overdue assignment deadlines
  * Grades or feedback published
* Provide a pluggable notification mechanism for future channels

---

### 6️⃣ Group Management Module

**Purpose:** Organize students into groups for collaboration or administration.

**Responsibilities:**

* Create, update, and delete groups
* Assign students to groups
* Associate group-specific resources or activities
* Support group-based visibility or access rules where needed

---

### 7️⃣ Community Management & Chat Module

**Purpose:** Provide community spaces around topics or institutions.

**Responsibilities:**

* Create, update, and delete communities
* Attach courses, groups, and content to each community
* Provide a real-time web chat endpoint (WebSocket) for each community

---

### 8️⃣ Security & Authentication Module

**Purpose:** Protect the API and data.

**Responsibilities:**

* JWT-based authentication (login, token validation)
* Spring Security configuration (filters, authentication providers)
* Method-level authorization using annotations (e.g. \`@PreAuthorize\`)
* Password encoding and secure credential handling

---

### 9️⃣ Persistence & Data Access Layer

**Purpose:** Interact with the database in a clean, abstracted way.

**Responsibilities:**

* Use Spring Data JPA repositories for entities like User, Course, Assignment, Submission, Group, Community, etc.
* Define query methods with pagination and sorting support
* Encapsulate ORM logic and mapping between entities and DTOs
* Support projections for optimized read queries


---

## 🛠️ Tech Stack

| Component     | Technology                            |
| ------------- |---------------------------------------|
| Backend       | Java 21, Spring Boot                  |
| Security      | Spring Security, JWT                  |
| Database      | MySQL or PostgreSQL                   |
| ORM           | Hibernate, Spring Data JPA            |
| Documentation | Swagger / OpenAPI, Insomnia           |
| Build Tool    | Maven                                 |
| Storage       | Local filesystem / Cloudinary / S3    |
| Real-time     | WebSocket (Spring), STOMP / Socket.IO |

---

## 📁 Project Structure

LearningManagementSystem
│
├── .idea
├── .mvn
├── logstash
│
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── maven
│       │           └── neuto
│       │               ├── annotation
│       │               ├── aspect
│       │               ├── config
│       │               ├── controller
│       │               ├── enum
│       │               ├── exception
│       │               ├── mapstruct
│       │               ├── model
│       │               ├── payload
│       │               ├── repository
│       │               ├── security
│       │               ├── service
│       │               ├── serviceImplement
│       │               ├── utils
│       │               └── NeuApplication.java
│       │
│       └── resources
│           ├── static
│           ├── templates
│           │   └── email
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-prod.yml
│           ├── logback-spring.xml
│           └── messages
│               ├── messages_en.properties
│               └── messages_bn.properties
│
└── pom.xml


---

## ⚙️ Setup Instructions

### 1. Clone

```bash
git clone https://github.com/Mahi12333/Learning-Management-System.git
cd Learning-Management-System
```

### 2. Environment variables

Create a `.env` or set system environment variables used by `application.yml`:

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/lms
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your-password
JWT_SECRET=your_jwt_secret_key_here
CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name   # optional
```

### 3. application.yml (example)

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

> **Note:** `ddl-auto: update` is convenient for development only. Use migrations (Flyway / Liquibase) for production.

### 4. Run (development)

```bash
mvn clean package
mvn spring-boot:run
```

Application will start on `http://localhost:8080` by default.

---

## 📌 API Documentation

If Swagger / OpenAPI is enabled:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📘 Endpoints Overview (examples)

### 🔐 Auth

* `POST /api/auth/register` — Register a new user
* `POST /api/auth/login` — Authenticate and receive JWT

### 📚 Courses

* `GET /api/courses` — List courses
* `POST /api/courses` — Create a course (TEACHER/ADMIN)
* `PUT /api/courses/{id}` — Update a course
* `DELETE /api/courses/{id}` — Remove a course

### 📝 Assignments

* `POST /api/assignments` — Create assignment
* `GET /api/assignments/course/{courseId}` — Assignments for a course

### 📤 Submissions

* `POST /api/submissions` — Submit assignment (multipart)
* `GET /api/submissions/student/{studentId}` — Student submissions

### 👥 Groups

* `POST /api/groups` — Create group
* `PUT /api/groups/{id}` — Update group
* `POST /api/groups/{id}/students` — Add students to group

### 🌐 Communities

* `POST /api/communities` — Create community
* `GET /api/communities/{id}/chat` — Web chat endpoint (WebSocket)

> Expand endpoints with pagination, sorting, and filters as needed.

---

## 🧪 Testing

* Unit tests: JUnit 5 + Mockito
* Integration tests: Spring Boot Test, Testcontainers (optional for DB)
* Postman collection: `/docs/postman_collection.json` (optional)

---

## ✅ Best Practices & Suggestions

* Use DTOs for all controller input/output.
* Centralize exception handling with `@ControllerAdvice`.
* Validate requests with `@Valid` and custom validators.
* Use role-based method security (`@PreAuthorize`) for fine-grained access.
* Externalize credentials (use Vault / secrets manager for prod).
* Add API rate limiting on sensitive endpoints.
* Use Flyway / Liquibase for schema migrations.
* Add logging + request tracing (Spring Sleuth / OpenTelemetry).

---

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repo
2. Create a feature branch
3. Open a PR describing your changes

Include tests and keep commits atomic.

---

## 📄 License

This project is licensed under the **MIT License** — see `LICENSE` file.

---

## 📞 Contact

Maintainer — Mahitosh Giri Name ([mahitoshgiri287.email@example.com](mailto:your.email@example.com))

---

