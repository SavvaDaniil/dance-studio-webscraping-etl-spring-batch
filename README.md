# Dance Studio Parser Batch

A Java-based ETL application built with Spring Boot and Spring Batch for extracting, transforming, and loading data from a dance studio website.

The project collects information about subscription prices, class schedules, workshops, teachers, dance styles, branches, and levels. The extracted data is stored in Apache Parquet files, uploaded to S3-compatible object storage using MinIO, transformed and normalized, and finally loaded into PostgreSQL.

The project is a Java/Spring Batch implementation of an earlier ETL pipeline originally developed using Python and Pandas.

---

## Architecture

The ETL pipeline follows a multi-stage architecture:

```text
Website
   │
   ▼
┌───────────┐
│  EXTRACT  │
└─────┬─────┘
      │
      ▼
 Raw Parquet
      │
      ▼
┌───────────┐
│ TRANSFORM │
└─────┬─────┘
      │
      ▼
Staging Parquet
      │
      ▼
┌───────────┐
│   LOAD    │
└─────┬─────┘
      │
      ▼
 PostgreSQL
```
Raw and staging Parquet files are stored in an S3-compatible MinIO object storage.

Example structure:

```text
raw/
└── 2026-08-20/
    ├── prices.parquet
    ├── schedules.parquet
    └── workshops.parquet

staging/
└── 2026-08-20/
    ├── prices.parquet
    ├── schedules.parquet
    └── workshops.parquet
```

## Technologies
- Java 21
- Spring Boot
- Spring Batch
- Playwright
- Jsoup
- Apache Parquet
- Apache Avro
- PostgreSQL
- JDBC
- Liquibase
- MinIO
- Maven
- Docker
- Docker Compose

## ETL Pipeline

The application is implemented as a Spring Batch job consisting of three sequential steps:
```text
danceStudioParserJob
        │
        ├── extractStep
        │
        ├── transformStep
        │
        └── loadStep
```
The job is configured using Spring Batch.

```java
@Bean
public Job danceStudioJob(
        JobRepository jobRepository,
        Step extractStep,
        Step transformStep,
        Step loadStep
) {
    return new JobBuilder("danceStudioParserJob", jobRepository)
            .start(extractStep)
            .next(transformStep)
            .next(loadStep)
            .build();
}
```

Each ETL stage is implemented as a Spring Batch Tasklet.

## Extract Stage

The Extract stage collects data from the target website.

The application extracts:
- Subscription prices
- Class schedules
- Workshops

Static HTML content is processed using Jsoup.

Dynamic content rendered by JavaScript is processed using Playwright.

The extracted data is converted into Java domain models and written to Apache Parquet files using Avro schemas.

Example raw models:

```text
Abonement
ScheduleRow
WorkshopBlock
```

The generated Parquet files are uploaded to MinIO.

## Transform Stage

The Transform stage reads raw Parquet files from MinIO and performs data normalization.

The transformation includes:
- Removing unnecessary characters
- Converting price strings to integers
- Parsing time values
- Parsing workshop dates
- Mapping weekdays to numeric values
- Normalizing branch names
- Normalizing dance style names
- Handling nullable fields
- Converting raw models into staging domain models

Examples:

```text
"₽5000" → 5000

"ШОССЕ" → "ШОССЕ ЭНТУЗИАСТОВ"

"хип хоп" → "Hip-hop"

"контемпорари" → "Contemporary"
```

After transformation, the data is stored as staging Parquet files and uploaded back to MinIO.

Example staging models:

```text
Price
Schedule
Workshop
```

## Load Stage

The Load stage reads staging Parquet files from MinIO and prepares normalized relational data.

The following entities are generated:
- Teachers
- Dance styles
- Branches
- Levels
- Prices
- Workshops
- Schedules

Example relationship:

```text
Teacher
   │
   └───────┐
           │
Style      │
   │       │
   └───────┼──► Schedule
           │
Branch     │
   │       │
   └───────┤
           │
Level──────┘
```

The processed data is inserted into PostgreSQL using JDBC and batch SQL operations.

---

# Spring Batch

Spring Batch manages the execution of the ETL pipeline.

The application defines a single job:

```text
danceStudioParserJob
```

The job consists of three sequential steps:

```text
extractStep
    ↓
transformStep
    ↓
loadStep
```

Each step is implemented using a Tasklet.

Example:

```java
@Bean
public Step extractStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ExtractTasklet extractTasklet
) {
    return new StepBuilder("extractStep", jobRepository)
            .tasklet(extractTasklet, transactionManager)
            .build();
}
```

Spring Batch stores metadata about job executions in PostgreSQL.

The metadata includes:
- Job instances
- Job executions
- Step executions
- Execution status
- Start and end timestamps
- Execution contexts
- Job parameters

Spring Batch automatically creates and manages tables with names similar to:

```text
BATCH_JOB_INSTANCE
BATCH_JOB_EXECUTION
BATCH_JOB_EXECUTION_PARAMS
BATCH_STEP_EXECUTION
BATCH_STEP_EXECUTION_CONTEXT
BATCH_JOB_EXECUTION_CONTEXT
```

---

## Job Parameters

The pipeline receives the extraction timestamp as a Spring Batch job parameter.

Example:

```java
LocalDateTime extractAt = LocalDateTime.now();

JobParameters jobParameters = new JobParametersBuilder()
        .addString("extractAt", extractAt.toString())
        .toJobParameters();

jobLauncher.run(danceStudioJob, jobParameters);
```
The same timestamp is used by all ETL stages.

This allows all generated files to use the same processing date.

Example:

```text
raw/2026-08-20/
staging/2026-08-20/
```

Inside a Tasklet, the parameter can be retrieved from the job context:

```java
String extractAtString = (String) chunkContext
        .getStepContext()
        .getJobParameters()
        .get("extractAt");

LocalDateTime extractAt = LocalDateTime.parse(extractAtString);
```

---

# Apache Parquet and Avro

Intermediate datasets are stored using Apache Parquet.

Apache Avro schemas are used for defining the structure of the Parquet records.

Example data flow:

```text
Java Domain Model
        │
        ▼
Avro GenericRecord
        │
        ▼
Parquet File
        │
        ▼
      MinIO
```

When reading files from MinIO, Parquet files are temporarily downloaded to the local filesystem.

```text
MinIO
  │
  ▼
Temporary File
  │
  ▼
ParquetReader
  │
  ▼
Java Objects
```

Temporary files are created inside:

```text
./data/temp
```

Example:

```java
java.nio.file.Path tempFile = Files.createTempFile(tempDir, "raw-prices-", ".parquet");
```

The temporary file is deleted after processing.

```java
finally {
    Files.deleteIfExists(tempFile);
}
```

---

# MinIO

MinIO is used as an S3-compatible object storage.

The application stores both raw and staging datasets.

Example structure:

```text
Bucket
│
├── raw
│   └── 2026-08-20
│       ├── prices.parquet
│       ├── schedules.parquet
│       └── workshops.parquet
│
└── staging
    └── 2026-08-20
        ├── prices.parquet
        ├── schedules.parquet
        └── workshops.parquet
```

The application uses the official MinIO Java SDK.

The bucket can be checked and created during application initialization.

Example:

```java
boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build()
    );

if (!exists) {
    minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
    );
}
```

---

# Database

PostgreSQL is used as the final storage layer.

The database contains normalized entities for:

```text
ds_example_prices
ds_example_teachers
ds_example_styles
ds_example_branches
ds_example_levels
ds_example_workshops
ds_example_schedules
```

Foreign key relationships are used to connect schedules and workshops with related entities.

Example:

```text
Schedule
├── teacher_id
├── style_id
├── branch_id
└── level_id
```

Data is inserted using JDBC.

Batch inserts are used to improve loading performance.

Example:

```java
String sql = "INSERT INTO ds_example_branches (id, name) VALUES (?, ?)";

try (PreparedStatement statement =
             connection.prepareStatement(sql)) {
    for (Branch branch : branches) {
        statement.setInt(1, branch.getId());
        statement.setString(2, branch.getName());
        statement.addBatch();
    }
    statement.executeBatch();
}
```

---

# Database Migrations

Liquibase is used for database schema management.

Database migrations are stored inside:

```text
src/main/resources/db/changelog
```

Example structure:

```text
db
└── changelog
    ├── changelog-master.xml
    └── changes
        └── 001-create-tables.xml
```

Liquibase tracks executed migrations and prevents the same migration from being executed multiple times.

---

# Configuration

Application configuration is stored in:

```text
src/main/resources/application.properties
```

Example:

```text
BASE_URL=https://example.com

MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET_NAME=ds-example-etl

spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Configuration classes use Spring's @Configuration annotation.

Example:

```java
@Configuration
public class MinioConfiguration {

    @Value("${MINIO_ENDPOINT}")
    private String endpoint;

    @Value("${MINIO_ACCESS_KEY}")
    private String accessKey;

    @Value("${MINIO_SECRET_KEY}")
    private String secretKey;

    @Value("${MINIO_BUCKET_NAME}")
    private String bucketName;

}
```

The MinIO client is created as a Spring Bean.

```java
@Bean
public MinioClient minioClient(
        MinioConfiguration configuration
) {
    return MinioClient.builder()
            .endpoint(configuration.getEndpoint())
            .credentials(
                    configuration.getAccessKey(),
                    configuration.getSecretKey()
            )
            .build();
}
```

The Spring container creates this bean once and injects the same MinioClient instance where required.

---

# Project Structure

Example project structure:

```text
src
└── main
    ├── java
    │   └── org
    │       └── savvadaniil
    │           ├── config
    │           ├── batch
    │           │   ├── BatchJobConfiguration.java
    │           │   └── JobLauncherRunner.java
    │           │
    │           ├── tasklets
    │           │   ├── ExtractTasklet.java
    │           │   ├── TransformTasklet.java
    │           │   └── LoadTasklet.java
    │           │
    │           ├── extract
    │           ├── transform
    │           ├── load
    │           │
    │           ├── repository
    │           ├── model
    │           ├── storage
    │           └── util
    │
    └── resources
        ├── application.properties
        └── db
            └── changelog
```

---

# Running the Application

Before running the application, make sure that:
- PostgreSQL is running.
- MinIO is running.
- The database configuration is correct.
- Liquibase migrations have been executed.
- Playwright browsers are installed.

Run the application using Maven:

```text
mvn spring-boot:run
```

Or build the project:

```text
mvn clean package
```

Then run the generated application.

---

# Docker

The infrastructure can be containerized using Docker Compose.

The project uses containers for:
- PostgreSQL
- MinIO

Example services:

```text
Docker Compose
│
├── PostgreSQL
│
└── MinIO
```

The Java application can be executed locally or added to the Docker Compose environment.

---

# Data Flow

The complete pipeline can be represented as:

```text
Dance Studio Website
        │
        ▼
┌──────────────────┐
│      EXTRACT     │
│ Jsoup / Playwright│
└────────┬─────────┘
         │
         ▼
   Raw Java Models
         │
         ▼
 Apache Parquet / Avro
         │
         ▼
        MinIO
         │
         ▼
┌──────────────────┐
│    TRANSFORM     │
│ Data Normalization│
└────────┬─────────┘
         │
         ▼
 Staging Java Models
         │
         ▼
 Apache Parquet / Avro
         │
         ▼
        MinIO
         │
         ▼
┌──────────────────┐
│       LOAD       │
│ JDBC / PostgreSQL│
└────────┬─────────┘
         │
         ▼
     PostgreSQL
```

---

# Features
- Automated web scraping
- Static HTML parsing with Jsoup
- Dynamic web scraping with Playwright
- Spring Batch job orchestration
- Sequential ETL steps
- Job parameters
- Batch execution metadata
- Apache Parquet storage
- Apache Avro schemas
- S3-compatible object storage
- MinIO integration
- Raw and staging data layers
- Data normalization
- Nullable field handling
- Date and time parsing
- JDBC batch inserts
- PostgreSQL relational database
- Liquibase database migrations
- Dockerized infrastructure

---

# Future Improvements

Possible improvements include:
- Retry policies for failed steps
- Automatic job scheduling
- Parallel processing
- Chunk-oriented processing instead of Tasklets
- Job restart support
- Custom retry and skip policies
- Monitoring and metrics
- Logging improvements
- Unit and integration tests
- Dockerizing the Spring Boot application
- CI/CD pipeline
- Support for multiple ETL jobs

---

# Learning Goals

This project was created to explore the Java ecosystem for data engineering and backend development.

The project demonstrates practical experience with:
- Java application architecture
- Spring Boot
- Spring Batch
- Dependency Injection
- JDBC
- Database migrations
- Apache Parquet
- Apache Avro
- S3-compatible object storage
- Web scraping
- Browser automation
- ETL pipeline design
- PostgreSQL
- Docker

The project also demonstrates the migration of a dataframe-based Python ETL pipeline to a strongly typed Java application using domain models and explicit processing stages.