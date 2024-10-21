# Use a specific Maven image to build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy the project files into the container
COPY pom.xml .

# Cache dependencies to improve build times
RUN mvn dependency:go-offline

# Copy the rest of the project files
COPY src ./src

# Build the Spring Boot application without running tests
RUN mvn clean package -DskipTests

# Use a lightweight OpenJDK image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the server port
EXPOSE 9799

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
