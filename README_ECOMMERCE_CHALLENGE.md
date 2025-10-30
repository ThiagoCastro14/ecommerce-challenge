# 🛍️ E-commerce Challenge API

A complete e-commerce management system built with **Spring Boot (Java 17)**, **MySQL**, **Kafka**, and **Elasticsearch**, following **Clean Architecture** principles.

This project was developed as part of a technical challenge to demonstrate expertise in backend development, microservice integration, and data consistency across distributed systems.

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.4 |
| **Database** | MySQL 8 |
| **Search Engine** | Elasticsearch 8 |
| **Messaging** | Apache Kafka |
| **Containerization** | Docker & Docker Compose |
| **Build Tool** | Maven 3.9+ |
| **API Docs** | Swagger / SpringDoc OpenAPI |

---

## 🎯 Features

- 🔐 **JWT Authentication**
  - Admin and User roles with permission-based access
- 🛒 **Product Management**
  - Full CRUD (Create, Read, Update, Delete)
  - Indexed in Elasticsearch for fast search
- 📦 **Order Management**
  - Multi-product order creation
  - Validation of stock availability
  - Status flow: `PENDING → PAID → CANCELED`
- 💬 **Event-Driven Updates**
  - Kafka `order.paid` event updates product stock
- 📊 **Reports**
  - Top 5 buyers by date range
  - Average ticket per user
  - Total revenue in the current month
- 🧩 **Architecture**
  - Clean separation of concerns: Controller → Service → Repository → Domain
  - Transactional integrity between MySQL and Kafka

---

## ⚙️ Project Structure

```
ecommerce-challenge/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/ecommerce/
 │   │   │   ├── config/          # Security, Kafka, Elasticsearch configs
 │   │   │   ├── controller/      # REST endpoints
 │   │   │   ├── service/         # Business logic
 │   │   │   ├── repository/      # JPA repositories
 │   │   │   ├── domain/          # Entities and enums
 │   │   │   └── event/           # Kafka producers/consumers
 │   │   └── resources/
 │   │       ├── application.yml  # Profiles (dev/prod)
 │   │       └── static/          # Swagger UI, etc.
 │   └── test/
 │       └── ...                  # Unit and integration tests
 ├── docker-compose.yml
 ├── pom.xml
 └── README.md
```

---

## 🧰 Prerequisites

Before starting, ensure you have installed:

- [Docker Desktop](https://www.docker.com/get-started)
- [Java 17+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [VS Code](https://code.visualstudio.com/) or IntelliJ IDEA

---

## 🐳 Local Environment Setup (Dev Profile)

### 1️⃣ Start the Infrastructure

From the project root, run:
```bash
docker compose up -d
```

This starts:
- MySQL → `localhost:3306`
- Kafka → `localhost:9092`
- Elasticsearch → `localhost:9200`

Check with:
```bash
docker ps
```

---

### 2️⃣ Run the Application

```bash
mvn spring-boot:run -s settings-ecommerce.xml
```

Access the API docs at:
👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

### 3️⃣ Stop the Environment

To stop the app:
```bash
Ctrl + C
```

To stop containers:
```bash
docker compose down
```

To stop and remove volumes (reset database):
```bash
docker compose down -v
```

---

## 🔐 API Authentication

**Endpoints:**

| Method | Endpoint | Role | Description |
|--------|-----------|------|--------------|
| `POST` | `/auth/signup` | Public | Register a new user |
| `POST` | `/auth/login` | Public | Login and get JWT token |
| `POST` | `/products` | Admin | Create product |
| `GET` | `/products/search` | User/Admin | Search products (via Elasticsearch) |
| `POST` | `/orders` | User | Create new order |
| `POST` | `/orders/pay/{id}` | User | Pay an order |
| `GET` | `/reports/top-users` | Admin | Get top 5 buyers |

---

## 🧩 Profiles

| Profile | Description |
|----------|--------------|
| `dev` | Local environment (Docker services) |
| `prod` | Remote or cloud environment (e.g., AWS, GCP) |

To run a specific profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 🧪 Example Request

### Create a User
```bash
POST http://localhost:8080/auth/signup
Content-Type: application/json

{
  "email": "user@dev.com",
  "password": "123456"
}
```

### Login
```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "user@dev.com",
  "password": "123456"
}
```

Returns:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 🧱 Build and Package

To build a JAR file:
```bash
mvn clean package
```

The output will be:
```
target/ecommerce-challenge-1.0.0.jar
```

To run manually:
```bash
java -jar target/ecommerce-challenge-1.0.0.jar
```

---

## 📄 License

This project was developed for technical evaluation purposes.  
All rights reserved © 2025 Thiago Maciel de Caldas Castro.

---

## 👤 Author

**Thiago Maciel de Caldas Castro**  
💻 Java Developer | Spring Boot | Kafka | Docker | MySQL | Elasticsearch  
📧 [Email](thiagocastro.as@gmail.com)  
🌍 [LinkedIn](https://www.linkedin.com/in/thiago-maciel-de-caldas-castro-830ba9195/)
