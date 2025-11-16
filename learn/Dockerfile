# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final, smaller image
# Use a reliable and official OpenJDK image from Eclipse Temurin
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/learn-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
