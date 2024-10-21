# Use an official Maven image to build the application
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app

# Copy the project files into the container
COPY pom.xml .
# Copy dependencies to leverage Docker caching and avoid rebuilding them every time.
RUN mvn dependency:go-offline

COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# Use a lightweight OpenJDK image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar from the Maven stage
COPY --from=build /app/target/*.jar app.jar

# Expose the server port
EXPOSE 9799

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
