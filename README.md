# Sensor Data Interpreter

This project is a prototype application for Turkish Cargo, designed to collect and interpret high-volume sensor data from cargo aircraft. The application, named `SensorDataInterpreter`, consumes sensor data from a central messaging queue (Apache Kafka), processes it asynchronously, enriches it with external data, and persists it into a database.

The system is designed to be resilient, scalable, and observable, handling fluctuating data volumes and ensuring a clear strategy for handling message failures.

## Features

- **Asynchronous & Concurrent Processing**: Consumes messages from Kafka and delegates the processing of each individual message to a dedicated `ThreadPoolExecutor` for high concurrency.
- **Data Processing & Enrichment**:
    - Separates and persists statistical sensor readings and operational events into distinct database tables.
    - Enriches operational data by querying a separate, external database for aircraft-specific calibration factors.
- **Resilient Architecture**:
    - Implements a **manual Dead Letter Queue (DLQ)** pattern. Any message that fails during processing is caught and explicitly published to a DLQ topic for later analysis.
- **Scalability**: Designed to scale both vertically within a single instance using a thread pool, and horizontally by running multiple application instances.
- **RESTful API**: Exposes a REST endpoint to query the processed location history of a specific device.
- **Centralized Exception Handling**: Provides standardized, user-friendly error responses for all API endpoints using `@RestControllerAdvice`.
- **API Documentation**: Integrated **Swagger (OpenAPI 3)** for interactive API documentation and testing.
- **Application Monitoring**: Includes **Spring Boot Actuator** for monitoring application health and metrics.

## Tech Stack

- **Java 21**
- **Spring Boot 3**
- **Apache Kafka**: Messaging Queue
- **PostgreSQL**: Primary and External Databases
- **Spring Data JPA / Hibernate**: Data Persistence
- **MapStruct**: DTO-Entity Mapping
- **Lombok**: Boilerplate Code Reduction
- **Maven**: Build Tool
- **Docker & Docker Compose**: Development Environment
- **JUnit 5 & Mockito**: Unit Testing
- **Swagger (springdoc-openapi)**: API Documentation
- **Spring Boot Actuator**: Application Monitoring

---

## Architectural Design and System Resilience

This section addresses the core design principles of the application, focusing on its ability to handle real-world challenges in a distributed environment, based on the **asynchronous processing model** implemented.

### Distributed Systems Design & Performance Optimization

#### How are retries and message loss handled?

The system adopts a **fail-fast approach** combined with a manual DLQ strategy to minimize message loss, rather than a traditional retry mechanism.

1.  **No Automatic Retries**: The consumer is designed to process each message once. If a persistent error occurs (e.g., database constraint violation, invalid data), retrying would likely fail again.
2.  **Manual Dead Letter Queue (DLQ)**: The core of the resilience strategy lies in the `processMessage` method. It contains a `try-catch` block. If `processingService.processSensorData(message)` throws any exception, the `catch` block executes. Instead of letting the message be re-processed, this block immediately publishes the original, failed message to a dedicated DLQ topic (`turkish-cargo-sensors.dlq`).
3.  **Message Acknowledgment**: The consumer uses manual acknowledgment (`ack.acknowledge()`). After submitting all messages in a batch to the `ThreadPoolExecutor`, it immediately acknowledges the batch. This means the system prioritizes throughput, accepting the risk that a message might fail in the background after being acknowledged. The DLQ mechanism is the safeguard against losing this failed message.

#### How is back-pressure handled for traffic fluctuations?

The system implements a form of back-pressure at the consumer level to manage situations where the message processing rate cannot keep up with the ingestion rate.

1.  **Bounded Thread Pool Queue**: The `ThreadPoolExecutor` is created with a fixed-size internal queue. When a traffic spike occurs, messages are submitted to this queue.
2.  **Rejected Execution Handling**: If both the threads and the queue of the `ThreadPoolExecutor` become full, attempting to submit a new task will throw a `RejectedExecutionException`.
3.  **Caller-Runs Policy**: This exception is caught within the consumer's `for` loop. The fallback strategy is to execute the task (`processMessage`) **directly on the caller thread** (the Kafka listener thread itself). This synchronous execution effectively slows down or pauses the consumption of new messages from Kafka, as the listener thread is now busy processing, creating a natural back-pressure effect and preventing the application's memory from being overwhelmed.

### Fault Tolerance & Resilience

#### How is resilience against Kafka or database errors achieved?

-   **Database/Processing Errors**: As described above, any exception thrown from the `SensorDataProcessingService` is caught. The error is logged, and the problematic message is immediately forwarded to the DLQ. This prevents a single "poison pill" message from crashing a worker thread or blocking other messages.
-   **Kafka Unavailability**: The underlying Spring Kafka framework handles broker unavailability. The listener container will attempt to reconnect to the Kafka cluster until it becomes available again, at which point consumption will resume.

#### How are compensating transactions handled?

A **compensating transaction** is an action that programmatically reverses a previously completed action when a larger, multi-step process fails.

In the current scope of this application, the data flow is a one-way ingestion pipeline. The `processSensorData` service method is annotated with `@Transactional`, ensuring that the saving of `StatisticalSensorData` and `OperationalSensorData` happens atomically (all or nothing) for a single message.

If the transaction fails, it is rolled back by Spring. The exception is then caught by the consumer, and the original message is sent to the DLQ. Therefore, a classic compensating transaction is not required for this workflow, as no partial state is ever committed to the database.

### Scalability & Load Balancing

#### How does the system scale with growing data volume?

The system is designed to scale using a two-dimensional approach:

1.  **Internal Scaling (Vertical)**: Within a single application instance, the `ThreadPoolExecutor` allows for concurrent processing of many messages, limited by the configured number of threads and the capacity of the underlying resources (CPU, database connection pool). This allows one instance to effectively utilize a multi-core environment.
2.  **External Scaling (Horizontal)**: The primary scalability method is running multiple instances of the `SensorDataInterpreter` application. As they share the same `group-id`, they form a Kafka Consumer Group. Kafka automatically distributes the topic partitions among these running instances. This means the total message load is divided across multiple machines, allowing the system to scale out to handle virtually any volume, provided the topic has a sufficient number of partitions.

#### How is load balancing between the web service and message queue handled?

Load balancing is handled independently and appropriately for each component, as they are decoupled:

1.  **Message Queue (Kafka) Load Balancing**: Kafka handles load distribution through its **partitioning and consumer group protocol**. When multiple application instances are running, Kafka acts as the "load balancer" by assigning partitions to each consumer, distributing the message load.
2.  **Web Service (REST API) Load Balancing**: In a production environment, multiple instances of the application would be run behind a traditional load balancer (like Nginx or an AWS ALB). Incoming HTTP requests would be distributed across the available instances, ensuring high availability and responsiveness for the API.

This decoupling ensures that a heavy load on the data ingestion side (Kafka) does not directly impact the performance or availability of the API, and vice-versa.

---

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- **Java 21** or higher
- Apache Maven 3.6+
- Docker and Docker Compose

### 1. Build the Application

Clone the repository and build the project using Maven. This will download all dependencies and compile the source code.

```bash
git clone https://github.com/yusufoglu218/sensor-data-interpreter.git
cd sensor-data-interpreter
mvn clean install
```

### 2. Start the Infrastructure

```bash
git clone https://github.com/yusufoglu218/sensor-data-interpreter.git
cd sensor-data-interpreter
mvn clean install
```

- A Kafka broker on port `9092`.
- A Zookeeper instance.
- A primary PostgreSQL database (`turkish_cargo_db`) on port `5432`.
- An external PostgreSQL database (`calibration_db`) for calibration data on port `5433`.

### 3. Run the Application

You can run the Spring Boot application using your IDE or directly from the command line:

```bash
java -jar target/sensor-data-interpreter-0.0.1-SNAPSHOT.jar
```

## Usage and Endpoints

Once the application is running, you can interact with its various endpoints.

### API Documentation (Swagger UI)

The primary way to explore and test the API is through the interactive Swagger UI.
- **URL**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Here you can view all available endpoints, see their request/response models, and execute API calls directly from your browser.

### Main API Endpoint

#### Get Location History

- **Endpoint**: `GET /api/v1/locations/{deviceId}`
- **Description**: Retrieves the location history for a specific device within a given time range.
- **Query Parameters**:
    - `startTime` (required): The start of the time range in UTC ISO 8601 format (e.g., `2023-01-01T10:00:00Z`).
    - `endTime` (required): The end of the time range.
- **Example `curl` Request**:
  ```bash
  curl -X GET "http://localhost:8080/api/v1/locations/tk-12345-67890?startTime=2023-03-15T17:00:00Z&endTime=2023-03-15T18:00:00Z"

### Application Monitoring (Spring Boot Actuator)
Actuator provides several endpoints to monitor the health and state of the application. By default, Spring Boot 3 only exposes the `/health` endpoint.

To expose all endpoints during development, add the following to your `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
 ```       

Once exposed, you can access other useful endpoints:
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Metrics**: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)
- **Specific Metric (e.g., Kafka consumer lag)**: [http://localhost:8080/actuator/metrics/kafka.consumer.records.lag.avg](http://localhost:8080/actuator/metrics/kafka.consumer.records.lag.avg)
- **Environment Details**: [http://localhost:8080/actuator/env](http://localhost:8080/actuator/env)