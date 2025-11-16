# Stage 1: Build the application
# Use a Maven image that includes Java 21 to match the project's requirement
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final, smaller image
# Use a reliable and official OpenJDK image from Eclipse Temurin
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/learn-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
