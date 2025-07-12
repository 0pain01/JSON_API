# 📦 JSON Dataset API – Spring Boot + MySQL
This is a RESTful backend-only service built using Spring Boot and MySQL. It allows users to:

- ✅ Dynamically create and use multiple “datasets” as separate tables

- ✅ Insert records into dataset-specific tables

- ✅ Query records using groupBy and sortBy operators

- ✅ Validate inputs and handle errors (missing fields, duplicate IDs)

- ✅ Fully tested with JUnit 5

---

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot 3.5.x**
- **Spring JDBC (JdbcTemplate)**
- **MySQL 8.x**
- **Lombok**
- **JUnit 5 + Spring Boot Test** for testing

---

## 🚀 Features

| Feature                                | Status  |
|----------------------------------------|---------|
| Dynamic table creation per datasetName | ✅      |
| Insert record with validation          | ✅      |
| Handle duplicate primary-key errors    | ✅      |
| Query: groupBy on supported fields     | ✅      |
| Query: sortBy on supported fields      | ✅      |
| Global error handling                  | ✅      |
| Unit test coverage                     | ✅      |

---

##🗄️ Table Schema (per dataset)
When you call POST /api/dataset/{datasetName}/record the service will ensure a table named {datasetName} exists with columns:

```bash
CREATE TABLE `{datasetName}` (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  age INT NOT NULL,
  department VARCHAR(100) NOT NULL
);
```

## ✅ Prerequisites

- Java 17+
- MySQL running locally (port 3306)
- Maven

### 🔧 Create Database using MySQL Workbench:
```bash
CREATE DATABASE json_dataset_db;
```
---

## 🧪 Running the Application

### 🔧 1. Clone the repo

```bash
git clone https://github.com/yourusername/json-dataset-api.git
cd json-dataset-api
```

### 🔧 2. Configure application.properties file

```bash
spring.datasource.url=jdbc:mysql://localhost:3306/json_dataset_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=none
```

### 🔧 3. Run the app

```bash
./mvnw clean install
./mvnw spring-boot:run
```

Server runs at:  
```
http://localhost:8080
```

---

## 🔌 API Endpoints

### ✅ Create a Chat Room
```http
POST /api/chatapp/chatrooms
```
**Request:**
```json
{ "roomName": "general" }
```

---

### ✅ Insert a Record
```http
POST /api/dataset/{datasetName}/record
```
**Request:**
```json
{
  "id": 1,
  "name": "John Doe",
  "age": 30,
  "department": "Engineering"
}
```
**Response:**
```json
{
  "message": "Record added successfully",
  "dataset": "employee_dataset",
  "recordId": 1
}
```

---

### ✅ Group Records
```http
GET /api/dataset/{datasetName}/query?groupBy=department
```
**Response:**
```json
{
  "groupedRecords": {
    "Engineering": [
      { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" },
      { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" }
    ],
    "Marketing": [
      { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" }
    ]
  }
}
```

---

### ✅ Sort Records
```http
GET /api/dataset/{datasetName}/query?sortBy=age&order=asc
```
**Response:**
```json
{
  "sortedRecords": [
    { "id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering" },
    { "id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing" },
    { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" }
  ]
}
```

---

### ❌ Error Handling
- **Duplicate ID**
```json
HTTP 400 Bad Request
{
  "error": "Record with ID 1 already exists in dataset 'employee_dataset'",
  "status": 400,
  "timestamp": "2025-07-12T10:00:00"
}
```
- **Missing or Blank Fields**
```json
HTTP 400 Bad Request
{
  "status": 400,
  "timestamp": "2025-07-12T10:05:00",
  "errors": {
    "name": "Name is required and cannot be blank",
    "department": "Department is required and cannot be blank",
    "age": "Age is required and cannot be null"
  }
}
```
- **Unsupported groupBy or sortBy**
```json
HTTP 400 Bad Request
{
  "error": "Unsupported groupBy field: salary",
  "status": 400,
  "timestamp": "2025-07-12T10:10:00"
}
```

---

## 🧪 Run Unit Tests

```bash
./mvnw test
```

---

## ✅ Sample Test Cases Covered

| Test Case                                | Status |
|------------------------------------------|--------|
| Insert valid record                      | ✅     |
| Insert duplicate ID → error              | ✅     |
| Missing name, department, or age → error | ✅     |
| groupBy=department → groups correctly    | ✅     |
| groupBy=age → groups by age              | ✅     |
| groupBy=invalid → error                  | ✅     |
| sortBy=age asc/desc → sorts correctly    | ✅     |
| sortBy=name and sortBy=department        | ✅     |
| sortBy=invalid or order=invalid → error  | ✅     |
| exception handler -> JSON error format   | ✅     |

---
