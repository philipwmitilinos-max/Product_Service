# Product Service

## Overview

Product Service is a Spring Boot microservice responsible for managing products in the webshop system.

It provides a REST API for retrieving products, creating products, deleting products and decreasing product stock.

## Responsibilities

Product Service handles:

* Creating new products.
* Retrieving all products.
* Retrieving a product by ID.
* Deleting products.
* Decreasing product stock for order items.
* Persisting product information in a database.
* Protecting API endpoints using JWT authentication.

## Technologies

* Java 17
* Spring Boot 4.0.6
* Spring Web / Spring MVC
* Spring Data JPA
* Hibernate
* PostgreSQL
* H2 Database
* Spring Security
* OAuth2 Resource Server
* JWT
* Jakarta Validation
* Maven
* JUnit 5
* Mockito
* Springdoc OpenAPI

## Database

The application connects to a database configured through environment variables.

| Variable      | Description             |
| ------------- | ----------------------- |
| `DB_URL`      | Database connection URL |
| `DB_USERNAME` | Database username       |
| `DB_PASSWORD` | Database password       |

PostgreSQL and H2 are included as dependencies. The database used at runtime depends on the configured connection URL.

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`, which updates the database schema based on the JPA entities.

### Initial Test Data

The application includes a `data.sql` file that inserts a test product into the `products` table during SQL initialization.

The test product has the following values:

* **Name:** Testprodukt
* **Description:** En enkel testprodukt för H2/Spring.
* **Price:** 99.90
* **Stock:** 10

SQL initialization is enabled through `spring.sql.init.mode=always`. The setting `spring.jpa.defer-datasource-initialization=true` ensures that SQL initialization is deferred until after Hibernate has initialized the database schema.

## Port

The default port is `8081`.

The port can be changed using the `SERVER_PORT` environment variable.

## Environment Variables

Configure the following environment variables before starting the application:

| Variable               | Description                           | Default                                  |
| ---------------------- | ------------------------------------- | ---------------------------------------- |
| `SERVER_PORT`          | Port used by the application          | `8081`                                   |
| `DB_URL`               | Database connection URL               | No default                               |
| `DB_USERNAME`          | Database username                     | No default                               |
| `DB_PASSWORD`          | Database password                     | No default                               |
| `JWT_ISSUER_URI`       | Expected JWT issuer                   | `https://auth.stenhager.com`             |
| `JWT_SET_URI`          | URL used to retrieve JWT signing keys | `https://auth.stenhager.com/auth/jwks`   |
| `JWT_ACCEPTED_ISSUERS` | Accepted JWT issuer values            | `test-issuer,https://auth.stenhager.com` |

Do not commit database credentials or other secrets to the repository.

## Running Locally

### Prerequisites

* Java 17 or a compatible configured Java environment.
* The Maven Wrapper included in the repository.
* A database configured through the environment variables.
* A reachable JWT issuer and JWK Set endpoint if authenticated API requests need to be tested.

### Windows

Set the required environment variables, open a terminal in the project root, and run:

```powershell
mvnw.cmd spring-boot:run
```

### Linux/macOS

Set the required environment variables, open a terminal in the project root, and run:

```bash
./mvnw spring-boot:run
```

The application starts on port `8081` by default.

## API Endpoints

The controller exposes the following endpoints:

| Method   | Endpoint                   | Description                    |
| -------- | -------------------------- | ------------------------------ |
| `GET`    | `/products`                | Retrieve all products          |
| `GET`    | `/products/{id}`           | Retrieve a product by ID       |
| `POST`   | `/products`                | Create a product               |
| `DELETE` | `/products/{id}`           | Delete a product               |
| `POST`   | `/products/stock/decrease` | Decrease stock for order items |

Authenticated requests must include a valid bearer token when required by the application's security configuration.

## Related Repositories

* [Auth Service](https://github.com/conniponni/Auth-server)
* [Gateway](https://github.com/conniponni/webshop-gateway)
* [Order Service](https://github.com/sedina-imamovic/order-service-webshop)
* [Frontend](https://github.com/Stefanvill/agil-frontend)

## API Documentation

The project includes Springdoc OpenAPI. The exact documentation URL depends on the application's configuration and security rules.
