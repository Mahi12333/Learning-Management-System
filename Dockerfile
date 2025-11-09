# Builder stage - Build the JAR inside the container
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Run Maven to build the application
RUN ./mvnw clean package -DskipTests

# Final stage - Run the JAR
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/neutopiya-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5002

# Set default command
ENTRYPOINT ["java", "-jar", "app.jar"]
