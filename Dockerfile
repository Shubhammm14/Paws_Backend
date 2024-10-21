# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar file from the previous stage
COPY --from=build /app/target/Paws_Backend-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]