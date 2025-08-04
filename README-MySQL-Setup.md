# MySQL Database Setup for Lima Project

This document provides instructions for setting up and configuring the MySQL database for the Lima project.

## Prerequisites

1. MySQL Server 8.0 or higher installed
2. Basic knowledge of MySQL commands or MySQL Workbench

## Database Setup

### Option 1: Manual Setup

1. Install MySQL Server if not already installed
   - Download from [MySQL Official Website](https://dev.mysql.com/downloads/mysql/)
   - Follow the installation instructions for your operating system

2. Create a new database
   ```sql
   CREATE DATABASE limaauthservice;
   ```

3. Create a user (optional, you can use root but not recommended for production)
   ```sql
   CREATE USER 'limauser'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON limaauthservice.* TO 'limauser'@'localhost';
   FLUSH PRIVILEGES;
   ```

### Option 2: Using Docker

1. Pull the MySQL Docker image
   ```bash
   docker pull mysql:8.0
   ```

2. Run MySQL container
   ```bash
   docker run --name lima-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=limaauthservice -p 3306:3306 -d mysql:8.0
   ```

## Application Configuration

The application is already configured to connect to MySQL with the following default settings in `application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DB:limaauthservice}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${MYSQL_USER:root}
spring.datasource.password=${MYSQL_PASSWORD:password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Customizing Database Connection

You can customize the database connection by:

1. **Environment Variables**: Set the following environment variables:
   - `MYSQL_HOST`: MySQL server hostname (default: localhost)
   - `MYSQL_PORT`: MySQL server port (default: 3306)
   - `MYSQL_DB`: Database name (default: limaauthservice)
   - `MYSQL_USER`: MySQL username (default: root)
   - `MYSQL_PASSWORD`: MySQL password (default: password)

2. **Direct Configuration**: Edit the `application.properties` file directly

## Hibernate Configuration

The application uses Hibernate with the following configuration:

```properties
# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

- `spring.jpa.hibernate.ddl-auto=update`: Automatically updates the database schema based on entity classes
- `spring.jpa.show-sql=true`: Shows SQL queries in the console for debugging

## Verifying the Setup

1. Start the application
2. Check the logs for successful database connection
3. If you see any database-related errors, verify:
   - MySQL server is running
   - Database credentials are correct
   - Database exists
   - Network connectivity to the database server

## Troubleshooting

1. **Connection Refused**: Ensure MySQL is running and accessible on the configured host and port
2. **Access Denied**: Verify username and password
3. **Unknown Database**: Ensure the database has been created
4. **Table Doesn't Exist**: If using `spring.jpa.hibernate.ddl-auto=validate`, ensure tables match entity definitions