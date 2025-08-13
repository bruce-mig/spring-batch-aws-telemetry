# Spring Boot AWS  - Multipart Download with Observability

A Spring Boot application demonstrating AWS S3 multipart file download capabilities with comprehensive observability features including monitoring, metrics collection, and distributed tracing.

## 🚀 Features

### Core Functionality
- **AWS S3 Multipart Download**: Efficient downloading of large files from S3 using AWS Transfer Manager
- **Spring Batch Processing**: ETL pipeline for processing downloaded CSV sales data
- **PostgreSQL Integration**: Data persistence with JPA and Hibernate
- **File Processing**: CSV parsing and data transformation with chunked processing

### Observability & Monitoring
- **Prometheus Metrics**: Custom metrics collection and export
- **Grafana Dashboards**: Pre-configured Spring Batch monitoring dashboards
- **Prometheus Push Gateway**: Metrics pushing for batch jobs
- **Spring Boot Actuator**: Health checks and application monitoring endpoints
- **Zipkin Tracing**: Distributed tracing for request flow analysis

### Infrastructure
- **Docker Compose**: Complete local development environment
- **MinIO**: S3-compatible object storage for local testing
- **PostgreSQL**: Primary database for sales data
- **Full Observability Stack**: Prometheus, Grafana, Zipkin, Push Gateway

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────┐    ┌──────────────┐
│   MinIO (S3)    │───▶│ Spring Boot │───▶│ PostgreSQL  │
│   File Storage  │    │ Application │    │   Database   │
└─────────────────┘    └─────────────┘    └──────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────┐
│                Observability Stack                      │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────────┐ │
│  │Prometheus│ │ Grafana │  │ Zipkin  │  │Push Gateway │ │
│  └─────────┘  └─────────┘  └─────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## 🛠️ Technology Stack

- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Batch** - ETL processing
- **Spring Cloud AWS 3.3.1** - AWS S3 integration
- **AWS SDK S3 Transfer Manager** - Multipart downloads
- **PostgreSQL** - Data persistence
- **Micrometer** - Metrics collection
- **Prometheus** - Metrics storage
- **Grafana** - Visualization
- **Zipkin** - Distributed tracing
- **Docker & Docker Compose** - Containerization

## 🚦 Getting Started

### Prerequisites
- Java 21
- Maven 3.6+
- Docker and Docker Compose
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/bruce-mig/spring-batch-aws-telemetry.git
cd spring-batch-aws-telemetry
```

### Configure environment

Use the convenience script to export environment variables used by Docker Compose and the Spring app:

```bash
source ./set-env.sh
```

Important variables set by the script:
- `MINIO_ROOT_USER`, `MINIO_ROOT_PASSWORD` — MinIO root credentials (for admin/`mc admin` actions)
- `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY` — Access keys the application will use
- `MINIO_URL` — S3 endpoint, defaults to `http://localhost:9000`
- `DB_URL` — JDBC URL for the application, e.g. `jdbc:postgresql://localhost:5432/sales`
- `DB_NAME`, `DB_USER`, `DB_PASSWORD` — Used by the Postgres container

### 2. Start Infrastructure Services
```bash
docker-compose up -d
```

This will start:
- **PostgreSQL** (port 5432)
- **MinIO** (ports 9000, 9001)
- **Prometheus** (port 9090)
- **Grafana** (port 3000)
- **Zipkin** (port 9411)
- **Push Gateway** (port 9091)

### 3. Configure MinIO
1. Access MinIO Console: http://localhost:9001
2. Login with credentials: `admin` / `admin#1234`
3. Create bucket named `sales-info`
4. Upload CSV files with prefix `2024` for testing

### 4. Build and Run the Application
```bash
mvn clean install
mvn spring-boot:run
```

## 📊 Monitoring & Observability

### Grafana Dashboard
- **URL**: http://localhost:3000
- **Default Credentials**: admin/admin
- **Features**: 
  - Spring Batch job metrics
  - Job duration tracking
  - Success/failure rates
  - System performance metrics

### Prometheus Metrics
- **URL**: http://localhost:9090
- **Custom Metrics**:
  - `spring_batch_job_seconds_max` - Job execution time
  - `spring_batch_job_seconds_count` - Job execution count
  - Custom application metrics via Micrometer

### Zipkin Tracing
- **URL**: http://localhost:9411
- **Features**: Request tracing across S3 operations and batch processing

### Application Health
- **Actuator Endpoint**: http://localhost:8080/actuator
- **Health Check**: http://localhost:8080/actuator/health
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus

## 🔧 Configuration

### Application Properties
Key configuration parameters in `application.yaml`:

```yaml
# Database Configuration
spring:
  datasource:
    url: ${DB_URL}
    username: root
    password: root

# AWS S3 Configuration
spring:
  cloud:
    aws:
      s3:
        endpoint: ${MINIO_URL}
        region: af-south-1
        path-style-access-enabled: true
        bucket: sales-info
      credentials:
        access-key: ${MINIO_ACCESS_KEY}
        secret-key: ${MINIO_SECRET_KEY}

# Prometheus Configuration
management:
  prometheus:
    metrics:
      export:
        pushgateway:
          address: localhost:9091
          job: multipart-download
```

### Batch Processing
- **Chunk Size**: 20 records per transaction
- **File Format**: CSV with comma delimiter
- **Processing Steps**:
  1. Download file from S3
  2. Parse CSV data
  3. Transform to entities
  4. Persist to PostgreSQL

## 📁 Project Structure

```
src/
├── main/java/pt/bayonne/sensei/multipartdownload/
│   ├── domain/          # JPA entities
│   ├── dto/             # Data transfer objects
│   ├── job/             # Spring Batch job configuration
│   ├── mapper/          # Data mapping utilities
│   ├── service/         # Business logic services
│   └── task/            # Batch tasklets
├── main/resources/
│   └── application.yaml
└── test/
    └── java/            # Unit tests
```

## 🔍 Key Components

### CustomS3Client
Handles S3 operations with transfer manager for efficient multipart downloads.

### ImportSalesInfoJob
Spring Batch job configuration with two main steps:
1. **Download Step**: Downloads files from S3
2. **Processing Step**: Parses and persists data

### DownloadFileTask
Tasklet responsible for initiating S3 file downloads.

## 🚀 Running Batch Jobs

The application automatically processes files prefixed with "2025" from the configured S3 bucket. Monitor job execution through:

1. **Application Logs**: Real-time processing information
2. **Grafana Dashboard**: Visual metrics and performance data
3. **Prometheus**: Raw metrics data
4. **Database**: Processed records in the `sales` table

## 🐳 Docker Services

| Service | Port | Purpose |
|---------|------|---------|
| PostgreSQL | 5432 | Primary database |
| MinIO | 9000/9001 | S3-compatible storage |
| Prometheus | 9090 | Metrics collection |
| Grafana | 3000 | Visualization dashboard |
| Zipkin | 9411 | Distributed tracing |
| Push Gateway | 9091 | Batch metrics pushing |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🏷️ Tags

`spring-boot` `aws` `s3` `batch-processing` `observability` `prometheus` `grafana` `zipkin` `docker` `postgresql` `microservices` `monitoring`
