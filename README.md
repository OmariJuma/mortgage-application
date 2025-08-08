
# Mortgage Application Backend Service

---

## 🚀 Overview

This project is a Spring Boot-based backend service designed to simplify and streamline mortgage loan processing. It includes features such as:

- Submission and management of loan applications.
- Upload and retrieval of documents through AWS S3.
- Kafka-based event-driven architecture.
- Role-Based Access Control (RBAC) using JWT to secure API endpoints.
- Decision-making workflows for loan officers to approve or reject applications.

---

## 🛠️ Features

1. **Loan Application Management**: RESTful APIs to create, read, and filter mortgage applications.
2. **Document Handling via AWS S3**: Integration with AWS S3 for scalable storage and retrievability of uploaded documents.
3. **Kafka Integration**: Seamlessly communicate events such as application creation, updates, fetching, and decision approvals using Apache Kafka.
4. **Role-Based Access Control (RBAC)**:
   - Two roles (`APPLICANT`, `OFFICER`).
   - Endpoints secured with JWT authentication and authorization.
5. **JWT-Based Authentication**: Tokenized session management for stateless communication.
6. **Integrated CI/CD**: Supports automated testing, builds, and deployments via GitHub Actions.

---

## 🏗️ Architecture

The backend uses a layered architecture with the following key components:

- **Spring Boot** for RESTful APIs.
- **PostgreSQL** database for persistent storage.
- **AWS S3** for storing uploaded documents securely.
- **Apache Kafka** for managing asynchronous event-driven operations.
- **JWT** (JSON Web Tokens) for secure authentication and role-based authorization.

### Architecture Diagram

```
[Frontend]
   |
   v
[REST API (Spring Boot)] <--> [Kafka Broker (Apache Kafka)] <--> [AWS S3]
   |
   v
[PostgreSQL Database]
```

---

## ⚙️ Application Configuration

The configuration is managed through the `application.properties` file.

### 1. Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mortgage
spring.datasource.username=your-database-username
spring.datasource.password=your-database-password
spring.jpa.hibernate.ddl-auto=none
```

### 2. JWT Configuration

```properties
jwt.secret=your-super-secret-jwt-key
jwt.expiration=86400000
```

### 3. AWS S3 Configuration

```properties
aws.s3.region=your-aws-region
```

### 4. Kafka Configuration

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=mortgage-group
```

Make sure to replace the placeholder values with your actual configurations.

---

## ✅ CI Badge

For CI/CD pipeline integration, you can add the following badge in your repository:

```markdown
[![CI/CD Pipeline](https://github.com/OmariJuma/mortgage-application/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/OmariJuma/mortgage-application/actions)
```

Replace the badge URL with your specific GitHub repository and action workflow URL.

---

## 👟 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

1. **Java 23** .
2. **Docker** for running the container
3. **Apache Maven** for build and dependency management.
4. an active AWS S3 bucket.

---

### 1. Clone the Repository

Clone the repository to your local system:

```shell script
git clone https://github.com/OmariJuma/mortgage-application.git
cd mortgage-application
```

---

### 2. Configure Properties

Edit the `application.properties` file in `src/main/resources` with the following:

#### Database:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mortgage
spring.datasource.username=postgres
spring.datasource.password=root
```

#### AWS S3:

Provide your AWS credentials by configuring your `.aws/credentials` file (recommended):

```
[default]
aws_access_key_id=your-access-key
aws_secret_access_key=your-secret-key
region=your-region
```

Alternatively, you can provide credentials through environment variables or directly in the `application.properties` file.

#### Kafka:

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

---

### 3. Start Docker compose

```bash
docker-compose up -d
```
It will start PostgreSQL database, Zookeeper, and Kafka broker.
`


---

### 4. Run the Application

Use the following Maven command to start the application:

```shell script
mvn spring-boot:run
```

The application will be accessible at:

```
http://localhost:8081
```

---

## 📜 Useful CURL Commands

### 1. Submit a Loan Application

```shell script
curl -X POST http://localhost:8081/api/v1/applications \
-H "Authorization: Bearer your-jwt-token" \
-H "Content-Type: application/json" \
-d '{
  "applicantId": "123",
  "nationalId": "1234567890",
  "amount": 100000,
  "status": "PENDING",
  "documents": [
    {
      "fileName": "file.pdf",
      "filePath": "/documents/file.pdf",
      "documentType": "PASSPORT",
      "fileType": "pdf"
    }
  ]
}'
```

### 2. Fetch Application by ID

```shell script
curl -X GET http://localhost:8081/api/v1/applications/{id} \
-H "Authorization: Bearer your-jwt-token"
```

### 3. Approve/Reject a Loan Application

```shell script
curl -X PATCH http://localhost:8081/api/v1/applications/{id}/decision \
-H "Authorization: Bearer your-jwt-token" \
-H "Content-Type: application/json" \
-d '{
  "decision": "APPROVED",
  "comment": "Criteria met"
}'
```

### 4. Retrieve All Applications

```shell script
curl -X GET "http://localhost:8081/api/v1/applications?status=PENDING&page=0&size=10" \
-H "Authorization: Bearer your-jwt-token"
```

---

## 📦 Kafka Topic Schema

### Kafka Topics

| Topic Name              | Description                                     |
|--------------------------|-------------------------------------------------|
| `loan.applications`      | Events triggered during loan application.       |
| `application-created`    | Event triggered after a loan is created.        |
| `decision-created`       | Event triggered when an application is decided. |

### Example Schema

```json
{
  "event": "CREATE",
  "traceId": "12345-abc-6789",
  "version": "1.0",
  "timestamp": "2024-01-01T10:00:00",
  "payload": {
    "applicationId": "d1eaa5bb-ca7f-4eaa-ac32-1d21e5aaf123",
    "status": "PENDING",
    "amount": 50000,
    "nationalId": "987654321"
  }
}
```

---

## 📝 Testing

### 1. Unit Tests

Run unit tests for services and repository:

```shell script
mvn test
```

### 2. Integration Tests

Validate end-to-end workflows:

```shell script
mvn verify
```

---

## 🚀 Deployment

### 1. Dockerize the App

Build a Docker image:

```shell script
docker build -t mortgage-app .
docker run -p 8081:8081 mortgage-app
```

### 2. AWS S3 Configuration

Ensure your S3 bucket is properly configured. Update bucket policies if needed.

### 3. Deploy Kafka

Use managed Kafka services like **Confluent Cloud** or deploy Kafka in production.

---

## 🛠️ Troubleshooting

1. **Database Connection Issues**:
   Ensure PostgreSQL is running, and the credentials in `application.properties` are accurate.

2. **Kafka Connection Errors**:
   Confirm that Kafka is running on the specified bootstrap servers.

3. **AWS S3 Issues**:
   Verify credentials in your `.aws/credentials` file or via environment variables.

