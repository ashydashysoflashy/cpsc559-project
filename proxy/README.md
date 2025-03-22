# Proxy Service for Distributed To-Do List App

## Overview
This proxy service acts as a gateway for routing API requests from clients to the appropriate backend server. It is designed to facilitate fault tolerance, replication, and load balancing for a distributed system.

## Features
- Routes API requests from clients (frontend React project) to the primary backend server.
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
backend.primary.url=http://localhost:8081
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
│   │   │               │   └── ApplicationConfig.java  # Configuration for accessing or updating application properties
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

### Update the Primary Server Url
Updates the url to which the proxy with forward requests.
```sh
curl -X POST http://localhost:8079/updatePrimary -H "Content-Type: text/plain" -d "http://localhost:8081" 
```

### Get the Primary Url
Gets the current primary server url. This endpoint is primarily for debugging purposes.
```sh
curl -X GET "http://localhost:8079/getPrimary"
```

## Proxy Logic
### Request Forwarding
- Extracts the full path from the client request.
- Constructs the appropriate target URL using `backend.primary.url` in `application.properties`.
- Forwards necessary headers (`Authorization`, `Content-Type`).
- Forwards the request body.

## Debugging (optional)
Enable debug logs in `application.properties`:
```properties
logging.level.org.springframework.web=DEBUG
```
Run the proxy and check logs for forwarded requests:
```sh
tail -f logs/proxy.log
```