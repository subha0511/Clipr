# Stage 1: Build the Spring Boot application
FROM maven:3.8.5-openjdk-17-slim AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml .

RUN mvn dependency:resolve

COPY src ./src

# Build the Spring Boot application (this will download dependencies and compile the app)
RUN mvn clean package -DskipTests

# Stage 2: Prepare the runtime environment
FROM openjdk:17-jdk-slim AS runtime

# Add the wait-for-it script
ADD wait-for-it.sh /usr/bin/wait-for-it
RUN chmod +x /usr/bin/wait-for-it

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/uri-0.0.1-SNAPSHOT.jar application.jar

# Expose the application's port
EXPOSE 8080

# Set the entry point for the Spring Boot application
ENTRYPOINT ["/usr/bin/wait-for-it", "postgres:5432", "--", "java", "-jar", "application.jar"]
