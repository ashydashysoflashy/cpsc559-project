# Proxy Service for Distributed To-Do List App

## Overview
This proxy service acts as a gateway for routing API requests from clients to the appropriate backend server. It is designed to facilitate fault tolerance, replication, and load balancing for a distributed system.

## Features
- Routes API requests from clients (frontend React project) to the primary backend server.
- Supports replication to backup servers for write operations (`POST`, `PUT`, `DELETE`).
- Forwards headers, including authentication tokens, to maintain session status.
- Dynamically constructs the target URL based on incoming requests.

## Prerequisites

- **Java 17** – Ensure you have a JDK 17 installed.
- **Gradle** – This project uses Gradle (Groovy DSL). The Gradle Wrapper is included, so you don’t have to install Gradle separately.

## Getting Started
- To **build** the project run the following command:
```
./gradlew build
```

- To **run** the project run the following command:

```
./gradlew bootRun // For Mac users
gradle.bat        // For Windows users
```
- This will run the project at http://localhost:8079

## Setup & Configuration

### 1. Configure Environment Variables
Modify `application.properties` to set up the proxy's configuration:

```properties
# Proxy runs on port 8079
server.port=8079

# Primary backend (server) URL for local development
backend.primary.url=http://localhost:8080/api/api

# Backup backend URLs for replication (comma-separated)
backend.backups.urls=http://backup1:8080/api,http://backup2:8080/api
```

## API Usage
The proxy listens for any request on `http://localhost:8079` and forwards requests to the primary backend

### Example Request
#### 1. Fetch All To-Do Lists
```sh
curl -X GET "http://localhost:8079/api/api/todolists" -H "Authorization: Bearer <JWT_TOKEN>"
```

This should forward the exact same request, changing only the base URI/hostname.

#### Example:

A request to the proxy at: "http://localhost:8079/api/api/todolists, should be forwarded to the server at
"http://localhost:8080/api/api/todolists or whatever you define it to be in **application.properties**.


## Proxy Logic
### Request Forwarding
- Extracts the full path from the client request.
- Constructs the appropriate target URL using `backend.primary.url`.
- Forwards necessary headers (`Authorization`, `Content-Type`).

### Replication for Write Operations
For `POST`, `PUT`, `DELETE` requests:
- The primary backend processes the request.
- The request is also forwarded to backup servers for replication.
- If a backup server fails, an error is logged but does not disrupt the main request.

## Debugging (optional)
Enable debug logs in `application.properties`:
```properties
logging.level.org.springframework.web=DEBUG
```
Run the proxy and check logs for forwarded requests:
```sh
tail -f logs/proxy.log
```

## Future Potential Enhancements
- Implement automatic failover if the primary backend is unreachable.
- Add caching for read requests to improve performance.
- Implement a health-check mechanism for backup servers.

# Proxy Service for Distributed To-Do List App

## Overview
This proxy service acts as a gateway for routing API requests from clients to the appropriate backend server. It is designed to facilitate fault tolerance, replication, and load balancing for a distributed system.

## Features
- Routes API requests from clients (frontend React project) to the primary backend server.
- Supports replication to backup servers for write operations (`POST`, `PUT`, `DELETE`).
- Forwards headers, including authentication tokens, to maintain session status.
- Dynamically constructs the target URL based on incoming requests.

## Prerequisites

- **Java 17** – Ensure you have a JDK 17 installed.
- **Gradle** – This project uses Gradle (Groovy DSL). The Gradle Wrapper is included, so you don’t have to install Gradle separately.

## Getting Started
- To **build** the project run the following command:
```
./gradlew build
```

- To **run** the project run the following command:

```
./gradlew bootRun // For Mac users
gradle.bat        // For Windows users
```
- This will run the project at http://localhost:8079

## Local Development Setup

### Configure Environment Variables
You can modify `application.properties` to set up the proxy's configuration:

```properties
# Proxy runs on port 8079
server.port=8079

# Primary backend (server) URL for local development
backend.primary.url=http://localhost:8080/api/api

# Backup backend URLs for replication (comma-separated)
backend.backups.urls=http://backup1:8080/api,http://backup2:8080/api
```

## Project Configuration
- **Gradle (Groovy DSL)** manages dependencies and builds.
- **Spring Boot v3.4.3** with jar packaging.
- **Key Dependencies**:
    - Spring Boot Starter Web (For easy CRUD operations)
  
## Folder Structure

```
proxy-project
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── cpsc559
│   │   │           └── proxy
│   │   │               ├── ProxyApplication.java       # Main application class
│   │   │               ├── config
│   │   │               │   └── ProxyConfig.java        # Defines any configuration classes needed (e.g., RestTemplate)
│   │   │               └── controller
│   │   │                   └── ProxyController.java    # Forwards requests to backend servers
│   │   └── resources
│   │       └── application.properties                  # Proxy configuration (backend URLs, server.port, etc.)
├── build.gradle                                        # Build file (Groovy DSL)
├── gradlew                                             # Gradle wrapper (Unix)
├── gradlew.bat                                         # Gradle wrapper (Windows)
└── README.md                                           # This file
```

## API Usage
The proxy listens for any request on `http://localhost:8079` and forwards requests to the primary backend.

### Example Request
#### 1. Fetch All To-Do Lists
```sh
curl -X GET "http://localhost:8079/api/api/todolists" -H "Authorization: Bearer <JWT_TOKEN>"
```

This should forward the exact same request, changing only the base URI/hostname.

#### Example:

A request to the proxy at "http://localhost:8079/api/api/todolists" 

is forwarded to the server at
"http://localhost:8080/api/api/todolists"

## Proxy Logic
### Request Forwarding
- Extracts the full path from the client request.
- Constructs the appropriate target URL using `backend.primary.url` in `application.properties`.
- Forwards necessary headers (`Authorization`, `Content-Type`).
- Forwards the request body.

### Replication for Write Operations
For `POST`, `PUT`, `DELETE` requests:
- The primary backend processes the request.
- The request is also forwarded to all the backup servers for replication.
- If a backup server fails, an error is logged but does not disrupt the main request.

## Debugging (optional)
Enable debug logs in `application.properties`:
```properties
logging.level.org.springframework.web=DEBUG
```
Run the proxy and check logs for forwarded requests:
```sh
tail -f logs/proxy.log
```