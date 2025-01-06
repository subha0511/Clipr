
# URL Shortener Service

## Overview

This repository contains a Spring Boot application that provides a URL shortener service with analytics and authentication features. The application supports shortening URLs, managing users, and generating insights on user interactions. 

## Features

- Shorten long URLs into short URLs.
- Retrieve, list, and delete shortened URLs.
- User authentication and token-based authorization (JWT with token rotation).
- Advanced analytics:
  - Events grouped by user agent, referrer, country, IP, and click intervals.
- Pagination and filtering support for retrieving URLs.

## Technologies Used

- **Spring Boot**: Core framework for building the application.
- **PostgreSQL**: Main database for storing user and URL data.
- **Redis**: Cache for performance optimization.
- **ClickHouse**: Events database for analytics and reporting.
- **JWT**: Bearer token authentication with token rotation (refresh token support).

## API Documentation

The API is documented using OpenAPI v3. Key features include:

### Authentication Endpoints

- **`POST /auth/login`**: Log in or register a user. Generates access and refresh tokens.
- **`GET /auth/refresh`**: Refresh the access token using the refresh token.
- **`GET /auth/logout`**: Log out the user, invalidating all active tokens.

### URL Shortener Endpoints

- **`POST /urls/shorten`**: Create a new shortened URL.
- **`GET /urls/{id}`**: Retrieve a shortened URL by ID.
- **`DELETE /urls/{id}`**: Delete a shortened URL by ID.
- **`GET /urls/`**: Retrieve all URLs created by the user (supports pagination).

### Analytics Endpoints

- **`GET /analytics/user-agent/{shortUrl}`**: Get events grouped by user agent for a given short URL.
- **`GET /analytics/referrer/{shortUrl}`**: Get events grouped by referrer for a given short URL.
- **`GET /analytics/ip/{shortUrl}`**: Get events grouped by IP address for a given short URL.
- **`GET /analytics/country/{shortUrl}`**: Get events grouped by country for a given short URL.
- **`GET /analytics/click/{shortUrl}`**: Get click events grouped by interval for a given short URL.

## Setup and Installation

### Prerequisites

- Java 17 or higher
- PostgreSQL
- Redis
- ClickHouse
- Maven

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repository/url-shortener-service.git
   cd url-shortener-service
   ```

2. Configure the application properties:
   Update `application.yml` or `application.properties` with your database, Redis, and ClickHouse configurations.

3. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Access the application:
   - API Base URL: `http://localhost:8080`
   - OpenAPI Docs: `http://localhost:8080/swagger-ui.html`

## Authentication

The application uses **JWT Bearer Token Authentication** with token rotation. Users must provide an access token in the `Authorization` header for protected routes. Example:

```http
Authorization: Bearer <access_token>
```

Tokens can be refreshed using the `GET /auth/refresh` endpoint.

## Database Schema

### PostgreSQL (Main DB)
- User table
- URL table

### Redis (Cache)
- URL cache for fast retrieval

### ClickHouse (Events DB)
- Analytics tables for event tracking

# Future Improvements

- Implement rate limiting for API endpoints.
- Add support for custom short URLs and expiration time.

## Contributions

Contributions are welcome! Please create an issue or submit a pull request with your suggestions or changes.

## License

This project is licensed under the [MIT License](LICENSE).

